/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.NornStone;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** The original thirty-slot SPS seed, ore, and Norn-stone pouch. */
public class SeedPouch extends Bag {
	{
		image = ItemSpriteSheet.POUCH;
	}
	@Override public boolean canHold(Item item) {
		return (item instanceof Plant.Seed || item instanceof StoneOre || item instanceof NornStone)
				&& super.canHold(item);
	}
	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
