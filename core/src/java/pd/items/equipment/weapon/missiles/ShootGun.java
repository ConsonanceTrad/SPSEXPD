/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles;

import pd.atlas.IconEntry;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.equipment.wands.WandOfBlastWave;
import pd.items.equipment.weapon.SpsRangedWeapon;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;

public class ShootGun extends SpsRangedWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShootGun.class)
			.t("name", "科技霰弹枪")
			.t("ac_shoot", "射击")
			.t("ac_endshoot", "终结射击")
			.t("ac_reload", "装填")
			.t("reloading", "正在装填……")
			.t("full", "霰弹枪已经装满。")
			.t("prompt", "选择一个目标")
			.t("damage", "这件武器可以造成_%1$d~%2$d点伤害_。")
			.t("charge", "弹药：%1$d / %2$d。")
			.t("desc", "经过外星科技改装的霰弹枪。普通射击会破甲并击退目标；终结射击会清空弹仓，对目标及其周围生物造成伤害。");
	}




	public static final String AC_SHOOT = "SHOOT";
	public static final String AC_ENDSHOOT = "ENDSHOOT";
	public static final String AC_RELOAD = "RELOAD";
	public static final int FULL_CHARGE = 3;
	private static final String CHARGE = "charge";
	private int charge;

	{
		image = EquipmentEquipWeaponUniqueWeaponDict.ELECTROMAGNETIC_SHOTGUN;
		stackable = false;
		unique = true;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
		reinforced = true;
	}

	@Override public int min(int lvl) { return 5 + 3 * Math.max(0, lvl); }
	@Override public int max(int lvl) { return 10 + 5 * Math.max(0, lvl); }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public int damageRoll(Char owner) { return 0; }
	@Override public boolean isUpgradable() { return true; }

	public int gunDamageRoll(Char owner) {
		return owner instanceof Hero ? Hero.heroDamageIntRange(min(), max()) : Random.NormalIntRange(min(), max());
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SHOOT);
		actions.add(AC_ENDSHOOT);
		actions.add(AC_RELOAD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SHOOT.equals(action)) {
			curUser = hero;
			if (charge == 0) reload(hero);
			else GameScene.selectCell(shooter);
		} else if (AC_ENDSHOOT.equals(action)) {
			curUser = hero;
			if (charge == 0) reload(hero);
			else GameScene.selectCell(endShooter);
		} else if (AC_RELOAD.equals(action)) {
			if (!reload(hero)) GLog.n(Messages.get(this, "full"));
		} else super.execute(hero, action);
	}

	public boolean reload(Hero hero) {
		if (hero == null || charge >= FULL_CHARGE) return false;
		float reloadTime = (FULL_CHARGE - charge) / 2f;
		charge = FULL_CHARGE;
		if (hero.sprite != null) hero.sprite.showStatus(CharSprite.DEFAULT, Messages.get(this, "reloading"));
		hero.spendAndNext(reloadTime);
		updateQuickslot();
		return true;
	}

	public void reloadMagazine() { charge = FULL_CHARGE; updateQuickslot(); }
	public int charge() { return charge; }
	public void charge(int value) { charge = Math.max(0, Math.min(FULL_CHARGE, value)); updateQuickslot(); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Dungeon.level != null && attacker != null && defender != null) {
			int opposite = defender.pos + defender.pos - attacker.pos;
			Ballistica trajectory = new Ballistica(defender.pos, opposite, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(defender, trajectory, 1, false, false, this);
		}
		return super.proc(attacker, defender, damage);
	}

	@Override public String status() { return levelKnown ? charge + "/" + FULL_CHARGE : null; }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "damage", min(), max()) + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }

	public ShootAmmo ammo() { return new ShootAmmo(); }
	public ShootEndAmmo endAmmo() { return new ShootEndAmmo(); }

	private abstract class BaseShootAmmo extends MissileWeapon {
		BaseShootAmmo(IconEntry image) {
			this.image = image;
			tier = 1;
			spawnedForEffect = true;
		}
		@Override public int min(int lvl) { return ShootGun.this.min(); }
		@Override public int max(int lvl) { return ShootGun.this.max(); }
		@Override public int STRReq(int lvl) { return ShootGun.this.STRReq(); }
		@Override public int damageRoll(Char owner) { return ShootGun.this.gunDamageRoll(owner); }
		@Override protected void onThrow(int cell) {
			Char enemy = Actor.findChar(cell);
			if (enemy == null || enemy == curUser) Splash.at(cell, 0xCC99FFFF, 1);
			else if (!curUser.shoot(enemy, this)) Splash.at(cell, 0xCC99FFFF, 1);
		}
		@Override protected void rangedHit(Char enemy, int cell) { }
		@Override protected void rangedMiss(int cell) { }
	}

	public class ShootAmmo extends BaseShootAmmo {
		ShootAmmo() { super(SpecificPlaceHolderDict.SOMETHING_0); }
		@Override public int proc(Char attacker, Char defender, int damage) {
			Buff.affect(defender, ArmorBreak.class, 5f).level(30);
			damage = ShootGun.this.proc(attacker, defender, damage);
			return super.proc(attacker, defender, damage);
		}
		@Override public void cast(Hero user, int dst) {
			if (charge <= 0) return;
			charge--;
			updateQuickslot();
			super.cast(user, dst);
		}
	}

	public class ShootEndAmmo extends BaseShootAmmo {
		ShootEndAmmo() { super(SpecificPlaceHolderDict.SOMETHING_0); ACC = 1000f; }
		@Override public int proc(Char attacker, Char defender, int damage) {
			if (Dungeon.level != null) for (int offset : PathFinder.NEIGHBOURS8) {
				int cell = defender.pos + offset;
				if (!Dungeon.level.insideMap(cell)) continue;
				Char ch = Actor.findChar(cell);
				if (ch == null || ch == defender || ch == attacker || !ch.isAlive()) continue;
				Buff.affect(ch, ArmorBreak.class, 5f).level(30);
				ch.damage(Math.max(0, gunDamageRoll(attacker) - Random.IntRange(0, 1)), attacker);
			}
			int missing = Math.max(0, defender.HT - defender.HP);
			int divisor = defender.properties().contains(Char.Property.BOSS)
					|| defender.properties().contains(Char.Property.MINIBOSS) ? 6 : 3;
			defender.damage(Math.min(missing, defender.HT / divisor), ShootGun.this);
			return super.proc(attacker, defender, damage);
		}
		@Override public void cast(Hero user, int dst) {
			if (charge <= 0) return;
			charge = 0;
			updateQuickslot();
			super.cast(user, dst);
		}
	}

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null && charge > 0) ammo().cast(curUser, target); }
		@Override public String prompt() { return Messages.get(ShootGun.this, "prompt"); }
	};

	private final CellSelector.Listener endShooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null && charge > 0) endAmmo().cast(curUser, target); }
		@Override public String prompt() { return Messages.get(ShootGun.this, "prompt"); }
	};

	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge(bundle.getInt(CHARGE)); }
}
