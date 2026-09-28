/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class StoneCross extends NormalMeleeWeapon {
	public static final int FULL_CHARGE = 20;
	private static final String CHARGE = "charge";
	private int charge;

	public StoneCross() {
		super(5, .8f, 1.2f, 1, 50, 66, ItemSpriteSheet.SPS_STONE_CROSS);
	}

	@Override protected void applyLegacyUpgrade(Stats stats) {
		if (stats.accuracy < 1.2f) stats.accuracy += .05f;
		if (stats.accuracy > 1.2f && stats.delay > 1f) stats.delay -= .05f;
		stats.max++;
	}

	@Override public int damageRoll(Char owner) {
		int damage = super.damageRoll(owner);
		return charge >= FULL_CHARGE ? damage * 5 : damage;
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (charge >= FULL_CHARGE) charge = 0;
		charge++;
		updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	public int charge() { return charge; }
	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }
}
