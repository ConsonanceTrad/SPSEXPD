/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class GunOfSoldier extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunOfSoldier.class)
			.t("name", "脉冲手枪")
			.t("ac_use", "射击")
			.t("prompt", "选择一个目标。")
			.t("break", "脉冲手枪的充能不足。")
			.t("charge", "充能：%1$d / %2$d。")
			.t("desc", "来自未来的先进脉冲手枪，会根据目标已损失的生命造成额外伤害。");
	}

	public static final String AC_USE = "USE";
	public static final int FULL_CHARGE = 225;
	public static final int SHOT_COST = 75;
	private static final String CHARGE = "charge";

	private int charge;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_USE;
		unique = true;
		usesTargeting = true;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= SHOT_COST) actions.add(AC_USE);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			curUser = hero;
			if (charge < SHOT_COST) GLog.i(Messages.get(this, "break"));
			else GameScene.selectCell(shooter);
		} else super.execute(hero, action);
	}

	public SoldierAmmo ammo() { return new SoldierAmmo(); }
	public void gainCharge() { if (charge < FULL_CHARGE) charge++; }
	public int charge() { return charge; }
	public boolean consumeShot() {
		if (charge < SHOT_COST) return false;
		charge -= SHOT_COST;
		updateQuickslot();
		return true;
	}

	@Override public String status() { return Integer.toString(charge / SHOT_COST); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }

	public class SoldierAmmo extends MissileWeapon {
		{
			image = SpecificPlaceHolderDict.SOMETHING_0;
			ACC = 1000f;
			baseUses = 1;
			spawnedForEffect = true;
		}
		@Override public int damageRoll(Char owner) { return 0; }
		@Override public int min(int lvl) { return 0; }
		@Override public int max(int lvl) { return 0; }
		@Override protected void onThrow(int cell) {
			Char enemy = Actor.findChar(cell);
			if (enemy == null || enemy == curUser) {
				parent = null;
				Splash.at(cell, 0xCC99FFFF, 1);
			} else if (!curUser.shoot(enemy, this)) {
				Splash.at(cell, 0xCC99FFFF, 1);
			}
		}
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

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null && curUser != null) ammo().cast(curUser, target); }
		@Override public String prompt() { return Messages.get(GunOfSoldier.class, "prompt"); }
	};
}
