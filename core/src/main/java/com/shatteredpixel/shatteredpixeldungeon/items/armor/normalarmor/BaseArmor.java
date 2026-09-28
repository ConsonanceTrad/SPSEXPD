/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** Cosmetic armor used by the demon-contract warrior start. */
public class BaseArmor extends NormalArmor {
	public BaseArmor() {
		// Legacy upgrades cancelled their own DR changes, so this remains 0-0 at every level.
		super(0, 1f, 1f, 4, 0, 0, 0, 0, 0, ItemSpriteSheet.SPS_BASE_ARMOR);
	}
}
