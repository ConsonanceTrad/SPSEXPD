/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.RangeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** SPS-PD's thirty-slot container for ranged and thrown weapons. */
public class ArrowCollecter extends Bag {

	{
		image = ItemSpriteSheet.SPS_ARROW_COLLECTER;
	}

	@Override
	public boolean canHold(Item item) {
		return (item instanceof RangeWeapon || item instanceof MissileWeapon)
				&& super.canHold(item);
	}

	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
