/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ShadowEaterKey;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** The original portable training target, used as a thirty-slot equipment bag. */
public class HeartOfScarecrow extends Bag {

	{
		image = ItemSpriteSheet.HEART_OF_SCARECROW;
	}

	@Override
	public boolean canHold(Item item) {
		if (item instanceof MeleeWeapon || item instanceof Armor || item instanceof ShadowEaterKey) {
			return super.canHold(item);
		}
		return false;
	}

	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
