/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.rooms.special;

import pd.items.Item;
import pd.items.StoneOre;
import pd.items.bombs.DungeonBomb;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import watabou.utils.Point;
import watabou.utils.Random;

import java.util.ArrayList;

/** Hidden glass chamber from SPS-PD 0.9.8. */
public class SpsGlassRoom extends SpecialRoom {

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.GLASS_WALL);
		Painter.fill(level, this, 2, Terrain.EMPTY_SP);

		Point center = new Point((left + right) / 2, (top + bottom) / 2);
		int centerCell = level.pointToCell(center);
		Painter.set(level, center, Terrain.PEDESTAL);
		level.drop(randomNornStone(), centerCell);

		ArrayList<Integer> floorCells = cells(level, Terrain.EMPTY_SP);
		Random.shuffle(floorCells);
		int stones = Math.min(Random.IntRange(2, 3), floorCells.size());
		for (int i = 0; i < stones; i++) level.drop(new StoneOre(), floorCells.get(i));

		entrance().set(Door.Type.HIDDEN);
		level.addItemToSpawn(new DungeonBomb.DoubleBomb());
	}

	private ArrayList<Integer> cells(Level level, int terrain) {
		ArrayList<Integer> result = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.map[cell] == terrain && level.heaps.get(cell) == null) result.add(cell);
			}
		}
		return result;
	}

	private static Item randomNornStone() {
		return Random.oneOf(new GreenNornStone(), new BlueNornStone(), new OrangeNornStone(),
				new PurpleNornStone(), new YellowNornStone());
	}
}
