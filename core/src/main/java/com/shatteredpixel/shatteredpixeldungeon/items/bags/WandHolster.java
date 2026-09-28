/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfCourage;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfPower;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfWisdom;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode.RockCode;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.SpAmmo;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** The original thirty-slot SPS magic-weapon holster. */
public class WandHolster extends Bag {
	{
		image = ItemSpriteSheet.HOLSTER;
	}
	@Override public boolean canHold(Item item) {
		return (item instanceof Wand || item instanceof TriforceOfCourage
				|| item instanceof TriforceOfPower || item instanceof TriforceOfWisdom
				|| item instanceof SpAmmo || item instanceof GunWeapon || item instanceof RockCode)
				&& super.canHold(item);
	}
	@Override public boolean collect(Bag container) {
		if (!super.collect(container)) return false;
		if (owner != null) {
			for (Item item : items) {
				if (item instanceof Wand) ((Wand)item).charge(owner);
			}
		}
		return true;
	}
	@Override public void onDetach() {
		super.onDetach();
		for (Item item : items) {
			if (item instanceof Wand) ((Wand)item).stopCharging();
		}
	}
	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
