/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.items.Item;
import pd.items.misc.AutoPotion;
import pd.items.scrolls.ScrollOfMagicalInfusion;
import pd.items.scrolls.ScrollOfRegrowth;
import pd.plants.ReNepenth;
import pd.plants.Starflower;

/** Original 48x48 SPS teleport Sokoban map, journal destination 3. */
public class SokobanTeleportLevel extends SokobanCastle {
	public static final int ENTRANCE = 8 + WIDTH * 16;
	public static final int[] SENTINELS = {21 + WIDTH * 25, 42 + WIDTH * 42};
	public static final int[] PORTALS = {
			4 + WIDTH * 2, 16 + WIDTH * 5, 23 + WIDTH * 5, 32 + WIDTH * 5,
			4 + WIDTH * 6, 25 + WIDTH * 8, 42 + WIDTH * 8, 10 + WIDTH * 9,
			2 + WIDTH * 10, 3 + WIDTH * 17, 8 + WIDTH * 17, 17 + WIDTH * 17,
			37 + WIDTH * 17, 40 + WIDTH * 17, 37 + WIDTH * 20, 40 + WIDTH * 20,
			21 + WIDTH * 21, 25 + WIDTH * 22, 15 + WIDTH * 24, 6 + WIDTH * 44,
			23 + WIDTH * 44, 44 + WIDTH * 28
	};
	public static final int[] INITIAL_DESTINATIONS = {
			2 + WIDTH * 9, 24 + WIDTH * 8, 8 + WIDTH * 44, 37 + WIDTH * 16,
			31 + WIDTH * 5, 9 + WIDTH * 16, 9 + WIDTH * 16, 5 + WIDTH * 2,
			9 + WIDTH * 16, 9 + WIDTH * 16, 0, 9 + WIDTH * 16,
			40 + WIDTH * 16, 36 + WIDTH * 20, 36 + WIDTH * 17, 24 + WIDTH * 23,
			18 + WIDTH * 5, 9 + WIDTH * 16, 9 + WIDTH * 16, 9 + WIDTH * 16,
			9 + WIDTH * 16, 0
	};
	public static final int[] SWITCHES = {
			30 + WIDTH * 14, 35 + WIDTH * 16, 42 + WIDTH * 16, 35 + WIDTH * 21,
			42 + WIDTH * 21, 30 + WIDTH * 36, 32 + WIDTH * 36, 35 + WIDTH * 36,
			37 + WIDTH * 36, 27 + WIDTH * 41
	};
	public static final int[] SWITCH_PORTALS = {
			8 + WIDTH * 17, 44 + WIDTH * 28, 40 + WIDTH * 17, 40 + WIDTH * 17,
			8 + WIDTH * 17, 40 + WIDTH * 20, 15 + WIDTH * 24, 8 + WIDTH * 17,
			23 + WIDTH * 44, 6 + WIDTH * 44
	};
	public static final int[] SWITCH_DESTINATIONS = {
			9 + WIDTH * 9, 25 + WIDTH * 44, 36 + WIDTH * 20, 41 + WIDTH * 20,
			2 + WIDTH * 17, 22 + WIDTH * 21, 16 + WIDTH * 17, 13 + WIDTH * 24,
			0, 42 + WIDTH * 7
	};
	public static final int[] PRIZES = {
			8 + WIDTH * 18, 8 + WIDTH * 18, 8 + WIDTH * 18,
			8 + WIDTH * 18, 8 + WIDTH * 18, 8 + WIDTH * 18,
			36 + WIDTH * 28, 36 + WIDTH * 28, 36 + WIDTH * 28,
			36 + WIDTH * 28, 36 + WIDTH * 28, 36 + WIDTH * 28,
			36 + WIDTH * 28
	};

	@Override protected int[] layout() { return SokobanLayouts.SOKOBAN_TELEPORT_LEVEL; }
	@Override protected int entranceCell() { return ENTRANCE; }
	@Override protected int destinationIndex() { return 3; }
	@Override protected int[] portalCells() { return PORTALS; }
	@Override protected int[] initialPortalDestinations() { return INITIAL_DESTINATIONS; }
	@Override protected int[] portalSwitchCells() { return SWITCHES; }
	@Override protected int[] portalSwitchPortals() { return SWITCH_PORTALS; }
	@Override protected int[] portalSwitchDestinations() { return SWITCH_DESTINATIONS; }
	@Override protected int[] sentinelCells() { return SENTINELS; }
	@Override protected int[] prizeCells() { return PRIZES; }
	@Override protected int goldMinimum() { return 300; }
	@Override protected int goldMaximum() { return 500; }
	@Override protected void createFixedItems() { }

	@Override protected int nonKeyPrizeCount() { return 7; }

	@Override
	protected Item nonKeyPrize(int id) {
		switch (id) {
			case 0: return new AutoPotion();
			case 1: return new ReNepenth.Seed();
			case 2:
			case 6: return new Starflower.Seed();
			case 3:
			case 4: return new ScrollOfMagicalInfusion();
			case 5: return new ScrollOfRegrowth();
			default: return null;
		}
	}
}
