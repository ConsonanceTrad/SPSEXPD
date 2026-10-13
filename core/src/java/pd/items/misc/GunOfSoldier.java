/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.weapon.guns.GunFire;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentNonEquipDict;

/**
 * SPSEXPD: 星兵开局电磁手枪——归入「充能型枪械」：拥有弹匣、可以装填，
 * 额外在「本回合未开火」时每回合自然回充 0.5 发（显示取整）。
 * 仍保留原用法：不占武器槽、可在背包中直接射击、unique、不可升级。
 */
public class GunOfSoldier extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunOfSoldier.class)
			.t("name", "脉冲手枪")
			.t("ac_use", "射击目标")
			.t("ac_aim", "定向射击")
			.t("ac_reload", "填弹")
			.t("prompt", "选择一个目标。")
			.t("prompt_aim", "选择一个射击方向。")
			.t("break", "脉冲手枪的弹匣是空的。")
			.t("no_magazine", "你没有可以装填的通用弹匣。")
			.t("full", "弹匣已满。")
			.t("reloading", "填弹中……")
			.t("charge", "弹匣：%1$d / %2$d。")
			.t("desc", "来自未来的先进脉冲手枪，会根据目标已损失的生命造成额外伤害。\n\n弹匣会缓慢自然回充，也可以手动装填。");
	}



	public static final String AC_USE = "USE";
	public static final String AC_AIM = "AIM";
	public static final String AC_RELOAD = "RELOAD";

	/** 弹匣容量（发）。 */
	public static final int MAGAZINE = 3;
	/** 未开火时每回合自然回充的发数。 */
	public static final float REGEN_PER_TURN = 0.5f;
	public static final int PERFECT_STR = 10;
	public static final int RANGE = 6;
	public static final int NOISE = 5;
	private static final float RELOAD_TIME = 2f;

	private static final String CHARGE = "charge";

	private int charge;
	private float regenProgress = 0f;
	private boolean firedSinceLastCharge = false;

	private int lastTargetPos = -1;
	private float lastShotTime = -Float.MAX_VALUE;

	{
		image = EquipmentNonEquipDict.ELECTROMAGNETIC_PISTOL;
		defaultAction = AC_USE;
		unique = true;
		usesTargeting = true;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge > 0) {
			actions.add(AC_USE);
			actions.add(AC_AIM);
		}
		actions.add(AC_RELOAD);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			curUser = hero;
			if (charge <= 0) GLog.i(Messages.get(this, "break"));
			else GameScene.selectCell(targetShooter);
		} else if (AC_AIM.equals(action)) {
			curUser = hero;
			if (charge <= 0) GLog.i(Messages.get(this, "break"));
			else GameScene.selectCell(aimShooter);
		} else if (AC_RELOAD.equals(action)) {
			curUser = hero;
			reload(hero);
		} else super.execute(hero, action);
	}

	public SoldierAmmo ammo() { return new SoldierAmmo(); }
	public int charge() { return charge; }

	/** SPSEXPD: 未开火时每回合自然回充（开火后的那一回合不回充）。 */
	public void gainCharge() {
		if (firedSinceLastCharge) {
			firedSinceLastCharge = false;
			return;
		}
		regenProgress += REGEN_PER_TURN;
		while (regenProgress >= 1f) {
			regenProgress -= 1f;
			if (charge < MAGAZINE) charge++;
		}
		updateQuickslot();
	}

	public boolean consumeShot() {
		if (charge <= 0) return false;
		charge--;
		updateQuickslot();
		return true;
	}

	private void reload(Hero hero) {
		if (charge >= MAGAZINE) {
			GLog.n(Messages.get(this, "full"));
			return;
		}
		if (!consumeMagazine(hero)) {
			GLog.n(Messages.get(this, "no_magazine"));
			return;
		}
		charge = MAGAZINE;
		updateQuickslot();
		if (hero.sprite != null) hero.sprite.showStatus(pd.sprites.CharSprite.DEFAULT, Messages.get(this, "reloading"));
		//SPSEXPD: 装填状态 buff（提高装填的可视度）
		pd.actors.buffs.Buff.affect(hero, pd.actors.buffs.Reloading.class, RELOAD_TIME);
		hero.spendAndNext(RELOAD_TIME);
	}

	/** SPSEXPD: 消耗一个通用弹匣填满弹匣（无限弹药袋生效时免消耗）。 */
	public boolean consumeMagazine(Hero hero) {
		if (hero == null) return false;
		if (pd.actors.buffs.EndlessAmmo.isActive(hero)) return true;
		Item magazine = hero.belongings.getItem(pd.items.Magazine.class);
		if (magazine == null || !hero.belongings.backpack.contains(magazine)) return false;
		return magazine.detach(hero.belongings.backpack) != null;
	}

	private float shotDelay(Hero hero, int cell) {
		Char target = pd.actors.Actor.findChar(cell);
		boolean sameTarget = target != null && target.pos == lastTargetPos;
		float buffer = Math.max(0, PERFECT_STR - hero.STR()) * 0.5f;
		float wait = Math.max(0f, lastShotTime + buffer - Game.timeTotal);
		float delay = wait + (sameTarget ? 0f : 0.5f);
		lastTargetPos = target != null ? target.pos : -1;
		lastShotTime = Game.timeTotal + delay;
		return delay;
	}

	private void fire(Hero hero, int cell) {
		if (charge <= 0) return;
		float delay = shotDelay(hero, cell);
		firedSinceLastCharge = true;
		SoldierAmmo shot = new SoldierAmmo();
		shot.preDelay = delay;
		shot.cast(hero, cell);
		GunFire.alertNearby(hero.pos, NOISE);
	}

	private final CellSelector.Listener targetShooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || curUser == null) return;
			if (pd.actors.Actor.findChar(target) == null) return;
			fire(curUser, target);
		}
		@Override public String prompt() { return Messages.get(GunOfSoldier.class, "prompt"); }
	};

	private final CellSelector.Listener aimShooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || curUser == null) return;
			fire(curUser, target);
		}
		@Override public String prompt() { return Messages.get(GunOfSoldier.class, "prompt_aim"); }
	};

	@Override public String status() { return Integer.toString(charge); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, MAGAZINE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }

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

	public class SoldierAmmo extends MissileWeapon {
		/** 本次击发的回合代价（由 fire 预先算好）。 */
		float preDelay = -1f;

		{
			image = SpecificPlaceHolderDict.SOMETHING_0;
			ACC = 1000f;
			baseUses = 1;
			spawnedForEffect = true;
		}
		@Override public int damageRoll(Char owner) { return 0; }
		@Override public int min(int lvl) { return 0; }
		@Override public int max(int lvl) { return 0; }
		@Override public float castDelay(Char user, int cell) {
			return preDelay >= 0f ? preDelay : super.castDelay(user, cell);
		}
		@Override protected void onThrow(int cell) {
			if (curUser == null) return;
			GunFire.resolveShot(curUser, this, cell, RANGE, PERFECT_STR, 0);
		}
		@Override protected void rangedHit(Char enemy, int cell) { }
		@Override protected void rangedMiss(int cell) { }
		@Override public int proc(Char attacker, Char defender, int damage) {
			int cap = Char.hasProp(defender, Char.Property.BOSS) || Char.hasProp(defender, Char.Property.MINIBOSS)
					? defender.HT / 6 : defender.HT / 3;
			defender.damage(Math.min(Math.max(0, defender.HT - defender.HP), cap), GunOfSoldier.this);
			return super.proc(attacker, defender, damage);
		}
		@Override public void cast(Hero user, int dst) {
			if (consumeShot()) super.cast(user, dst);
		}
	}
}
