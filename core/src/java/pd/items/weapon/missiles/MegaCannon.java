/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.MechArmor;
import pd.actors.buffs.TargetShoot;
import pd.actors.hero.Hero;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.rings.RingOfSharpshooting;
import pd.items.weapon.SpsRangedWeapon;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class MegaCannon extends SpsRangedWeapon {
	public static final String AC_SHOOT = "SHOOT";
	public static final int FULL_CHARGE = 3;
	private static final String CHARGE = "charge";
	private int charge;
	{
		image = ItemSpriteSheet.SPS_MEGA_CANNON;
		ACC = 1f;
		DLY = 0.75f;
		RCH = 1;
		unique = true;
		reinforced = true;
		cursed = true;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
	}
	@Override public int min(int lvl) { return 1 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 5 + 3 * Math.max(0, lvl); }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public int damageRoll(Char owner) { return 0; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		gainCharge();
		return super.proc(attacker, defender, damage);
	}
	@Override public boolean isUpgradable() { return true; }
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SHOOT);
		return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (AC_SHOOT.equals(action)) { curUser = hero; GameScene.selectCell(shooter); }
		else super.execute(hero, action);
	}
	public void gainCharge() { if (charge < FULL_CHARGE) { charge++; updateQuickslot(); } }
	public int charge() { return charge; }
	public void charge(int value) { charge = Math.max(0, Math.min(FULL_CHARGE, value)); updateQuickslot(); }
	public int consumeCharge() {
		int power = charge;
		charge = 0;
		updateQuickslot();
		return power;
	}
	public int shotDamage(Char owner, int power) {
		int damage = owner instanceof Hero ? Hero.heroDamageIntRange(min(), max()) : Random.NormalIntRange(min(), max());
		if (owner.buff(TargetShoot.class) != null) damage = Math.round(damage * 1.5f);
		if (owner.buff(MechArmor.class) != null) damage = Math.round(damage * 1.5f);
		int bonus = Math.min(RingOfSharpshooting.levelDamageBonus(owner), 30);
		if (bonus > 0 && Random.Int(10) < 3) damage = (int)(damage * (1.5f + 0.25f * bonus));
		return Math.max(0, damage * Math.max(0, power));
	}
	public MegaAmmo ammo() { return new MegaAmmo(charge); }
	@Override public String status() { return Integer.toString(charge); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "damage", min(), max()); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge(bundle.getInt(CHARGE)); }

	public class MegaAmmo extends MissileWeapon {
		private final int power;
		MegaAmmo(int power) {
			this.power = Math.max(0, Math.min(FULL_CHARGE, power));
			image = power > 2 ? ItemSpriteSheet.SPS_MEGA_AMMO_LARGE
					: power > 1 ? ItemSpriteSheet.SPS_MEGA_AMMO_MEDIUM : ItemSpriteSheet.SPS_MEGA_AMMO_SMALL;
			tier = 1;
			ACC = 100f;
			spawnedForEffect = true;
		}
		@Override public int min(int lvl) { return 0; }
		@Override public int max(int lvl) { return MegaCannon.this.max(); }
		@Override public int STRReq(int lvl) { return 0; }
		@Override public int damageRoll(Char owner) { return shotDamage(owner, power); }
		@Override protected void onThrow(int cell) {
			Char enemy = Actor.findChar(cell);
			if (enemy == null || enemy == curUser) Splash.at(cell, 0xCC99FFFF, 1);
			else if (!curUser.shoot(enemy, this)) Splash.at(cell, 0xCC99FFFF, 1);
		}
		@Override protected void rangedHit(Char enemy, int cell) { }
		@Override protected void rangedMiss(int cell) { }
		@Override public void cast(Hero user, int dst) {
			if (charge <= 0) return;
			consumeCharge();
			super.cast(user, dst);
		}
	}
	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null && charge > 0) ammo().cast(curUser, target); }
		@Override public String prompt() { return Messages.get(MegaCannon.class, "prompt"); }
	};
}
