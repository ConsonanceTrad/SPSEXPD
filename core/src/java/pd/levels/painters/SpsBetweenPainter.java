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

		//SPSEXPD: 过渡层不属于五个标准区域，没有 decorate.png 装饰覆盖层。
		//残留的区域装饰格（RegionDecoPatch 等房型铺的 REGION_DECO/ALT）会露出
		//旧图集的 BLANK 帧（红色方块图案），而且按 STATUE 处理是实心的，走不进去。
		//这里统一清成普通空地：真正空白、可通行。必须放在随机装饰之后，避免又被改成 EMPTY_DECO。
		for (int cell = 0; cell < level.length(); cell++) {
			if (level.map[cell] == Terrain.REGION_DECO || level.map[cell] == Terrain.REGION_DECO_ALT) {
				level.map[cell] = Terrain.EMPTY;
			}
		}
	}
}
