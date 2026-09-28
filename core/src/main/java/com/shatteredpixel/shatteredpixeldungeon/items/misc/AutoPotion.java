/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** Original SPS auto-potion; its legacy AutoHealPotion buff contains no active logic. */
public class AutoPotion extends Ring {
	public AutoPotion() {
		anonymize();
		image = ItemSpriteSheet.AUTO_POTION;
	}

	@Override
	protected RingBuff buff() {
		return new AutoHealPotion();
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 500 * quantity;
	}

	public class AutoHealPotion extends RingBuff {
	}
}
