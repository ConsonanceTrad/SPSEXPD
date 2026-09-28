/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class GunOfSoldier extends Item {
	public static final String AC_USE = "USE";
	public static final int FULL_CHARGE = 225;
	public static final int SHOT_COST = 75;
	private static final String CHARGE = "charge";

	private int charge;

	{
		image = ItemSpriteSheet.LEGACY_SOLDIER_GUN;
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
			image = ItemSpriteSheet.LEGACY_SOLDIER_AMMO;
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
