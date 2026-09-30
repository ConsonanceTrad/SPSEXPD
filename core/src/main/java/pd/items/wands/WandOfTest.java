/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.wands;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.items.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/** A 99-charge tester wand with seven selectable elemental damage types. */
public class WandOfTest extends DamageWand {

	public static final String AC_ENERGY = "0";
	public static final String AC_FIRE = "1";
	public static final String AC_ICE = "2";
	public static final String AC_SHOCK = "3";
	public static final String AC_EARTH = "4";
	public static final String AC_LIGHT = "5";
	public static final String AC_DARK = "6";
	private static final String TYPE = "type";

	private int type;

	{
		image = ItemSpriteSheet.SPS_TEST_WAND;
		collisionProperties = Ballistica.MAGIC_BOLT;
	}

	@Override public int min(int lvl) { return 10 + lvl; }
	@Override public int max(int lvl) { return 10 + 2 * lvl; }
	@Override public int initialCharges() { return 99; }
	@Override public void updateLevel() { maxCharges = 99; curCharges = Math.min(curCharges, maxCharges); }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ENERGY);
		actions.add(AC_FIRE);
		actions.add(AC_ICE);
		actions.add(AC_SHOCK);
		actions.add(AC_EARTH);
		actions.add(AC_LIGHT);
		actions.add(AC_DARK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (isTypeAction(action)) {
			type(Integer.parseInt(action));
			return;
		}
		super.execute(hero, action);
	}

	private static boolean isTypeAction(String action) {
		return AC_ENERGY.equals(action) || AC_FIRE.equals(action) || AC_ICE.equals(action)
				|| AC_SHOCK.equals(action) || AC_EARTH.equals(action) || AC_LIGHT.equals(action)
				|| AC_DARK.equals(action);
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target == null) return;
		wandProc(target, chargesPerCast());
		target.damage(damageRoll(), damageType());
		if (target.sprite != null) target.sprite.burst(0xFF99CCFF, 2);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD's test wand has no battlemage on-hit effect.
	}

	public DamageType damageType() {
		switch (type) {
			case 1: return DamageType.FIRE_DAMAGE;
			case 2: return DamageType.ICE_DAMAGE;
			case 3: return DamageType.SHOCK_DAMAGE;
			case 4: return DamageType.EARTH_DAMAGE;
			case 5: return DamageType.LIGHT_DAMAGE;
			case 6: return DamageType.DARK_DAMAGE;
			default: return DamageType.ENERGY_DAMAGE;
		}
	}

	@Override
	public void wandUsed() {
		int before = curCharges;
		super.wandUsed();
		if (before > 0 && curCharges <= 0 && curUser != null) {
			curUser.spp += 100;
			detach(curUser.belongings.backpack);
		}
	}

	public int type() { return type; }
	public void type(int value) { type = Math.max(0, Math.min(6, value)); }

	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "ac_" + type); }
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TYPE, type);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		type(bundle.getInt(TYPE));
	}
}
