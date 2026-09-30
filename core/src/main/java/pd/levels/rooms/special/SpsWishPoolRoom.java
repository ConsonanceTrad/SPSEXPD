/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.rooms.special;

import pd.actors.mobs.npcs.TownNpc;
import pd.items.Dewdrop;
import pd.items.Generator;
import pd.items.Item;
import pd.items.RedDewdrop;
import pd.items.VioletDewdrop;
import pd.items.YellowDewdrop;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import render.utils.PathFinder;
import render.utils.Point;
import render.utils.Random;

import java.util.ArrayList;

/** Hidden SPS wishing pool with a preserved eight-cell water ring. */
public class SpsWishPoolRoom extends SpecialRoom {

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY);

		Point centerPoint = center();
		int center = level.pointToCell(centerPoint);
		for (int y = centerPoint.y - 2; y <= centerPoint.y + 2; y++) {
			for (int x = centerPoint.x - 2; x <= centerPoint.x + 2; x++) {
				Point point = new Point(x, y);
				if (!inside(point)) continue;
				int cell = level.pointToCell(point);
				int terrain = level.map[cell];
				if (terrain != Terrain.WALL && terrain != Terrain.WALL_DECO
						&& terrain != Terrain.DOOR && terrain != Terrain.SECRET_DOOR
						&& terrain != Terrain.GLASS_WALL) {
					Painter.set(level, cell, Terrain.EMPTY_SP);
				}
			}
		}

		ArrayList<Integer> water = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (level.insideMap(cell)) {
				Painter.set(level, cell, Terrain.WATER);
				water.add(cell);
			}
		}
		Painter.set(level, center, Terrain.STATUE);

		TownNpc hmdzl = new TownNpc().configure(TownNpc.Spec.HMDZL001);
		hmdzl.pos = center;
		level.mobs.add(hmdzl);

		Random.shuffle(water);
		for (int cell : water) level.drop(randomDew(), cell);

		ArrayList<Integer> rewardCells = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.map[cell] == Terrain.EMPTY_SP && level.heaps.get(cell) == null
						&& level.findMob(cell) == null) rewardCells.add(cell);
			}
		}
		if (!rewardCells.isEmpty()) level.drop(Generator.random(), Random.element(rewardCells));

		entrance().set(Door.Type.HIDDEN);
	}

	private static Item randomDew() {
		switch (Random.Int(21)) {
			case 0: case 1: case 2: case 3: return new Dewdrop();
			case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: return new YellowDewdrop();
			case 12: case 13: case 14: case 15: case 16: return new RedDewdrop();
			case 17: case 18: case 19: return new VioletDewdrop();
			default: return new pd.plants.Dewcatcher.Seed();
		}
	}
}
