/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles;

import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.MechArmor;
import pd.actors.buffs.TargetShoot;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.effects.Splash;
import pd.items.Heap;
import pd.items.equipment.rings.RingOfSharpshooting;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.missiles.throwing.EscapeKnive;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/** Izayoi's reusable knife set from SPS-PD. */
public class ManyKnive extends Weapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ManyKnive.class)
			.t("name", "十六夜的千支刀")
			.t("ac_shoot", "投掷")
			.t("shoot_prompt", "选择投掷目标")
			.t("desc", "由大量普通飞刀组成的飞刀组。")
			.t("damage", "这件高攻速武器可以造成_%1$d～%2$d点伤害_。");
	}



	public static final String AC_SHOOT = "SHOOT";

	{
		image = EquipmentEquipWeaponUniqueWeaponDict.MANY_KNIVE_0;
		stackable = false;
		unique = true;
		bones = false;
		reinforced = true;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return 2 + lvl; }
	@Override public int max(int lvl) { return 4 + lvl; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public boolean isUpgradable() { return true; }
	@Override public boolean isIdentified() { return true; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_EQUIP);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_SHOOT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SHOOT.equals(action)) {
			curUser = hero;
			GameScene.selectCell(shooter);
		} else {
			super.execute(hero, action);
		}
	}

	@Override
	public int damageRoll(Char owner) {
		int damage = Random.Int(min(), max());
		if (owner.buff(TargetShoot.class) != null) damage = (int)(damage * 1.5f);
		if (owner.buff(MechArmor.class) != null) damage = (int)(damage * 1.5f);
		int bonus = Math.min(RingOfSharpshooting.levelDamageBonus(owner), 30);
		if (bonus > 0 && Random.Int(10) < 3) {
			damage = (int)(damage * (1.5f + 0.25f * bonus));
			if (owner.sprite != null) owner.sprite.emitter().burst(Speck.factory(Speck.STAR), 8);
		}
		return Math.max(0, damage);
	}

	private int rangedProc(Char attacker, Char defender, int damage) {
		maybeDropEscapeKnife(defender);
		return super.proc(attacker, defender, damage);
	}

	boolean maybeDropEscapeKnife(Char defender) {
		if (Dungeon.level == null || defender == null || Random.Int(50) != 0) return false;
		Heap heap = Dungeon.level.drop(new EscapeKnive(1), defender.pos);
		if (heap != null && heap.sprite != null) heap.sprite.drop();
		return true;
	}

	@Override
	public String info() {
		return desc() + "\n\n" + Messages.get(this, "damage", min(), max());
	}

	public class KniveAmmo extends MissileWeapon {
		{
			image = SpecificPlaceHolderDict.SOMETHING_0;
			tier = 1;
			DLY = 0.25f;
			spawnedForEffect = true;
		}
		@Override public int min(int lvl) { return ManyKnive.this.min(); }
		@Override public int max(int lvl) { return ManyKnive.this.max(); }
		@Override public int STRReq(int lvl) { return 10; }
		@Override public int damageRoll(Char owner) { return ManyKnive.this.damageRoll(owner); }
		@Override protected void onThrow(int cell) {
			Char enemy = Actor.findChar(cell);
			if (enemy == null || enemy == curUser) Splash.at(cell, 0xCC99FFFF, 1);
			else if (!curUser.shoot(enemy, this)) Splash.at(cell, 0xCC99FFFF, 1);
		}
		@Override protected void rangedHit(Char enemy, int cell) { }
		@Override protected void rangedMiss(int cell) { }
		@Override public int proc(Char attacker, Char defender, int damage) {
			return rangedProc(attacker, defender, damage);
		}
	}

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target != null) new KniveAmmo().cast(curUser, target);
		}
		@Override public String prompt() { return Messages.get(ManyKnive.class, "shoot_prompt"); }
	};
}
