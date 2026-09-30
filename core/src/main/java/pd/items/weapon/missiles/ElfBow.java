/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.MechArmor;
import pd.actors.buffs.TargetShoot;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.items.rings.RingOfSharpshooting;
import pd.items.weapon.Weapon;
import pd.items.weapon.melee.start.DemonBlade;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import watabou.utils.Bundle;
import watabou.utils.Random;

import java.util.ArrayList;

public class ElfBow extends Weapon {

	public static final String AC_SHOOT = "SHOOT";
	public static final String AC_DRINK = "DRINK";
	private static final String CHARGE = "charge";
	private int charge;

	{
		image = ItemSpriteSheet.SPS_ELF_BOW;
		stackable = false;
		unique = true;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
		reinforced = true;
		ACC = 1f;
		DLY = 1f;
		RCH = 1;
	}

	@Override public int min(int lvl) { return 2 + 2 * Math.max(0, lvl); }
	@Override public int max(int lvl) { return 6 + 4 * Math.max(0, lvl); }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public boolean isUpgradable() { return true; }
	@Override public boolean isIdentified() { return true; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SHOOT);
		actions.add(AC_DRINK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SHOOT.equals(action)) {
			if (!isEquipped(hero)) GLog.i(Messages.get(this, "need_equip"));
			else { curUser = hero; GameScene.selectCell(shooter); }
		} else if (AC_DRINK.equals(action)) {
			if (!drink(hero)) GLog.i(Messages.get(this, "need_equip"));
		} else {
			super.execute(hero, action);
		}
	}

	public boolean drink(Hero hero) {
		if (hero == null || !isEquipped(hero)) return false;
		hero.spp += 10;
		charge++;
		if (charge > 10) {
			DemonBlade blade = new DemonBlade();
			blade.upgrade(3).identify();
			if (Dungeon.level != null) {
				Heap heap = Dungeon.level.drop(blade, hero.pos);
				if (heap.sprite != null) heap.sprite.drop();
			}
			if (hero.belongings.weapon == this) hero.belongings.weapon = null;
			charge = 0;
		}
		updateQuickslot();
		return true;
	}

	@Override
	public int damageRoll(Char owner) {
		Hero hero = owner instanceof Hero ? (Hero) owner : Dungeon.hero;
		int damage = Hero.heroDamageIntRange(min(), max());
		if (hero != null && hero.buff(TargetShoot.class) != null) damage = (int)(damage * 1.5f);
		if (hero != null && hero.buff(MechArmor.class) != null) damage = (int)(damage * 1.5f);
		int bonus = hero == null ? 0 : Math.min(RingOfSharpshooting.levelDamageBonus(hero), 30);
		if (bonus > 0 && Random.Int(10) < 3) damage = (int)(damage * (1.5f + 0.25f * bonus));
		if (hero != null) damage = (int)(damage * (1f + 0.1f * hero.magicSkill()));
		return Math.max(0, damage);
	}

	public ElfBowAmmo ammo() { return new ElfBowAmmo(); }
	public int charge() { return charge; }

	public class ElfBowAmmo extends MissileWeapon {
		{ image = ItemSpriteSheet.DART; tier = 1; baseUses = 100f; }
		@Override public int min(int lvl) { return ElfBow.this.min(); }
		@Override public int max(int lvl) { return ElfBow.this.max(); }
		@Override public int STRReq(int lvl) { return ElfBow.this.STRReq(); }
		@Override public int damageRoll(Char owner) { return ElfBow.this.damageRoll(owner); }
		@Override public boolean isUpgradable() { return false; }
		@Override public int proc(Char attacker, Char defender, int damage) {
			if (attacker instanceof Hero && damage > defender.HP) ((Hero) attacker).spp++;
			return super.proc(attacker, defender, damage);
		}
	}

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null) ammo().cast(curUser, target); }
		@Override public String prompt() { return Messages.get(ElfBow.this, "prompt"); }
	};

	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "damage", min(), max()); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, bundle.getInt(CHARGE)); }
}
