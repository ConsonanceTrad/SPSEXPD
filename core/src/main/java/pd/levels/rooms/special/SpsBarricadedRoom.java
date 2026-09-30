/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.rooms.special;

import pd.items.Generator;
import pd.items.Heap;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import watabou.utils.Random;

import java.util.ArrayList;

/** Hidden bookshelf maze and skeletal caches from SPS-PD 0.9.8. */
public class SpsBarricadedRoom extends SpecialRoom {

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY_SP);

		if (width() > height()) {
			for (int x = left + 2; x < right; x += 2) {
				Painter.fill(level, x, top + 2, 1, height() - 4, Terrain.BOOKSHELF);
			}
		} else {
			for (int y = top + 2; y < bottom; y += 2) {
				Painter.fill(level, left + 2, y, width() - 4, 1, Terrain.BOOKSHELF);
			}
		}

		ArrayList<Integer> floorCells = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.map[cell] == Terrain.EMPTY_SP) floorCells.add(cell);
			}
		}
		Random.shuffle(floorCells);
		int caches = Math.min(Random.IntRange(2, 3), floorCells.size());
		for (int i = 0; i < caches; i++) {
			level.drop(prize(), floorCells.get(i)).type = Heap.Type.SKELETON;
		}

		entrance().set(Door.Type.HIDDEN);
	}

	private static pd.items.Item prize() {
		pd.items.Item prize = Generator.random();
		if (prize != null) return prize;
		return Generator.random(Random.oneOf(Generator.Category.POTION,
				Generator.Category.SCROLL, Generator.Category.GOLD, Generator.Category.NORNSTONE));
	}
}
