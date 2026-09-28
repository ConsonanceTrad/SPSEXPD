/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KnowledgeBook;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.Egg;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.plants.ReNepenth;
import com.shatteredpixel.shatteredpixeldungeon.plants.StarEater;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;

/** Original 48x48 SPS puzzle collection, journal destination 4. */
public class SokobanPuzzlesLevel extends SokobanCastle {
	public static final int ENTRANCE = 15 + WIDTH * 11;
	public static final int[] SENTINELS = {33 + WIDTH * 30};
	public static final int[] PORTALS = {
			11 + WIDTH * 10, 32 + WIDTH * 15, 25 + WIDTH * 40,
			37 + WIDTH * 18, 45 + WIDTH * 33, 37 + WIDTH * 3, 43 + WIDTH * 2
	};
	public static final int[] INITIAL_DESTINATIONS = {
			0, 23 + WIDTH * 8, 23 + WIDTH * 8, 23 + WIDTH * 8,
			34 + WIDTH * 6, 23 + WIDTH * 8, 23 + WIDTH * 8
	};
	public static final int[] SWITCHES = {
			19 + WIDTH * 10, 19 + WIDTH * 6, 9 + WIDTH * 8, 16 + WIDTH * 37
	};
	public static final int[] SWITCH_PORTALS = {
			PORTALS[0], PORTALS[0], PORTALS[1], PORTALS[5]
	};
	public static final int[] SWITCH_DESTINATIONS = {
			30 + WIDTH * 16, 23 + WIDTH * 40, 37 + WIDTH * 16, 42 + WIDTH * 2
	};
	public static final int[] PRIZES = {
			15 + WIDTH * 11, 16 + WIDTH * 17, 16 + WIDTH * 35,
			20 + WIDTH * 38, 27 + WIDTH * 35, 33 + WIDTH * 31,
			11 + WIDTH * 10,
			41 + WIDTH * 2, 41 + WIDTH * 2, 41 + WIDTH * 2, 41 + WIDTH * 2,
			41 + WIDTH * 2, 41 + WIDTH * 2, 41 + WIDTH * 2, 41 + WIDTH * 2
	};

	@Override protected int[] layout() { return SokobanLayouts.SOKOBAN_PUZZLE_LEVEL; }
	@Override protected int entranceCell() { return ENTRANCE; }
	@Override protected int destinationIndex() { return 4; }
	@Override protected int[] portalCells() { return PORTALS; }
	@Override protected int[] initialPortalDestinations() { return INITIAL_DESTINATIONS; }
	@Override protected int[] portalSwitchCells() { return SWITCHES; }
	@Override protected int[] portalSwitchPortals() { return SWITCH_PORTALS; }
	@Override protected int[] portalSwitchDestinations() { return SWITCH_DESTINATIONS; }
	@Override protected int[] sentinelCells() { return SENTINELS; }
	@Override protected int[] prizeCells() { return PRIZES; }
	@Override protected int goldMinimum() { return 300; }
	@Override protected int goldMaximum() { return 500; }
	@Override protected int nonKeyPrizeCount() { return 9; }

	@Override
	protected Item nonKeyPrize(int id) {
		switch (id) {
			case 0: return new KnowledgeBook();
			case 1:
			case 8: return new ScrollOfMagicalInfusion();
			case 2: return new Egg();
			case 3:
			case 6: return new ReNepenth.Seed();
			case 4: return new Starflower.Seed();
			case 5:
			case 7: return new StarEater.Seed();
			default: return null;
		}
	}

	@Override
	protected void createFixedItems() {
		drop(new PotionOfLiquidFlame(), 9 + WIDTH * 24).type = com.shatteredpixel.shatteredpixeldungeon.items.Heap.Type.CHEST;
	}
}
