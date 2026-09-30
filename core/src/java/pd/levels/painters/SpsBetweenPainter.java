/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.painters;

import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.rooms.Room;
import render.utils.math.Random;

import java.util.ArrayList;

/** Applies the common SPS-PD decoration rules used by all five transition floors. */
public class SpsBetweenPainter extends RegularPainter {

	@Override
	protected void decorate(Level level, ArrayList<Room> rooms) {
		int width = level.width();
		for (int cell = width + 1; cell < level.length() - width - 1; cell++) {
			if (level.map[cell] == Terrain.WATER && Random.Int(25) == 0) {
				level.map[cell] = Terrain.OLD_HIGH_GRASS;
			} else if (level.map[cell] == Terrain.EMPTY && Random.Int(40) == 0) {
				level.map[cell] = Terrain.OLD_HIGH_GRASS;
			}
		}

		for (int cell = 0; cell < level.length(); cell++) {
			if (level.map[cell] == Terrain.EMPTY && Random.Int(10) == 0) {
				level.map[cell] = Terrain.EMPTY_DECO;
			} else if (level.map[cell] == Terrain.WALL && Random.Int(8) == 0) {
				level.map[cell] = Terrain.WALL_DECO;
			} else if (level.map[cell] == Terrain.SECRET_DOOR) {
				level.map[cell] = Terrain.DOOR;
			}
		}
	}
}
