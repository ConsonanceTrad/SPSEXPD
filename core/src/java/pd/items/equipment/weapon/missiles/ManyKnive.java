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
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/** Izayoi's reusable knife set from SPS-PD. */
public class ManyKnive extends Weapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ManyKnive.class)
			.t("name", "十六夜的千支刀")
			.t("ac_shoot", "投掷")
			.t("ac_reload", "装填")
			.t("shoot_prompt", "选择投掷目标")
			.t("no_energy", "炼金能量不足（装填需要 2 点）。")
			.t("no_ammo", "千支刀没有弹药了。")
			.t("full", "弹药已满。")
			.t("reloading", "装填中……")
			.t("desc", "由大量普通飞刀组成的飞刀组。\n\n每次装填消耗_2 点炼金能量_，充满_6 发_弹药；没有弹药时无法投掷。")
			.t("damage", "这件高攻速武器可以造成_%1$d～%2$d点伤害_。")
			.t("charge", "弹药：%1$d/%2$d");
	}



	public static final String AC_SHOOT = "SHOOT";
	public static final String AC_RELOAD = "RELOAD";

	/** SPSEXPD: 弹匣容量（发）。 */
	public static final int MAGAZINE = 6;
	/** SPSEXPD: 一次装填消耗的炼金能量。 */
	public static final int ENERGY_COST = 2;
	/** SPSEXPD: 装填耗时（回合）。 */
	public static final float RELOAD_TIME = 2f;

	private static final String CHARGE = "charge";

	private int charge;

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

	public int charge() { return charge; }
	public int fullCharge() { return MAGAZINE; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_EQUIP);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_SHOOT);
		actions.add(AC_RELOAD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SHOOT.equals(action)) {
			curUser = hero;
			//SPSEXPD: 无弹药时不能投掷
			if (charge <= 0) GLog.i(Messages.get(this, "no_ammo"));
			else GameScene.selectCell(shooter);
		} else if (AC_RELOAD.equals(action)) {
			curUser = hero;
			reload(hero);
		} else {
			super.execute(hero, action);
		}
	}

	/** SPSEXPD: 装填——消耗 2 点炼金能量，填充 6 发。 */
	public void reload(Hero hero) {
		if (charge >= MAGAZINE) {
			GLog.n(Messages.get(this, "full"));
			return;
		}
		if (!loadAmmo(hero)) {
			GLog.n(Messages.get(this, "no_energy"));
			return;
		}
		if (hero.sprite != null) hero.sprite.showStatus(pd.sprites.CharSprite.DEFAULT, Messages.get(this, "reloading"));
		//SPSEXPD: 装填状态 buff（提高装填的可视度）
		pd.actors.buffs.Buff.affect(hero, pd.actors.buffs.Reloading.class, RELOAD_TIME);
		hero.spendAndNext(RELOAD_TIME);
	}

	/** SPSEXPD: 扣炼金能量并填满弹药，不排回合（供内部与确定性测试调用）。 */
	public boolean loadAmmo(Hero hero) {
		if (charge >= MAGAZINE) return false;
		if (Dungeon.energy < ENERGY_COST) return false;
		Dungeon.energy -= ENERGY_COST;
		charge = MAGAZINE;
		updateQuickslot();
		return true;
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
		return desc() + "\n\n" + Messages.get(this, "damage", min(), max())
				+ "\n\n" + Messages.get(this, "charge", charge, MAGAZINE);
	}

	@Override
	public String status() { return charge + "/" + MAGAZINE; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(MAGAZINE, bundle.getInt(CHARGE)));
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
			if (target == null || charge <= 0) return;
			charge--;
			updateQuickslot();
			new KniveAmmo().cast(curUser, target);
		}
		@Override public String prompt() { return Messages.get(ManyKnive.class, "shoot_prompt"); }
	};
}
