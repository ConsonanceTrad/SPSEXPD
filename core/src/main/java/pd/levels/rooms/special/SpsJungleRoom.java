/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.rooms.special;

import pd.Dungeon;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Greatmoss;
import pd.items.Honeypot;
import pd.items.keys.IronKey;
import pd.items.weapon.missiles.buildblock.PlantPotBlock;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import pd.plants.BlandfruitBush;
import pd.plants.NutPlant;
import pd.plants.Plant;
import pd.plants.Seedpod;
import watabou.utils.PathFinder;
import watabou.utils.Random;

import java.util.ArrayList;

/** Locked SPS jungle filled with rare plants and great mosses. */
public class SpsJungleRoom extends SpecialRoom {

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.GRASS);

		entrance().set(Door.Type.LOCKED);
		level.addItemToSpawn(new IronKey(Dungeon.depth));

		ArrayList<Integer> free = grassCells(level);
		placeSeed(level, free, new Seedpod.Seed());
		placeSeed(level, free, new BlandfruitBush.Seed());
		placeSeed(level, free, new NutPlant.Seed());
		drop(level, free, new Honeypot());
		drop(level, free, new PlantPotBlock());

		int mosses = ((right - left - 1) * (bottom - top - 1)) / 10;
		for (int i = 0; i < mosses; i++) {
			ArrayList<Integer> candidates = mossCells(level);
			if (candidates.isEmpty()) break;
			placeMoss(level, Random.element(candidates));
		}
	}

	private void placeSeed(Level level, ArrayList<Integer> free, Plant.Seed seed) {
		if (free.isEmpty()) return;
		int cell = free.remove(Random.Int(free.size()));
		level.plant(seed, cell);
	}

	private void drop(Level level, ArrayList<Integer> free, pd.items.Item item) {
		if (free.isEmpty()) return;
		level.drop(item, free.remove(Random.Int(free.size())));
	}

	private ArrayList<Integer> grassCells(Level level) {
		ArrayList<Integer> result = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.map[cell] == Terrain.GRASS && level.heaps.get(cell) == null
						&& level.plants.get(cell) == null && level.findMob(cell) == null) result.add(cell);
			}
		}
		return result;
	}

	private ArrayList<Integer> mossCells(Level level) {
		ArrayList<Integer> result = grassCells(level);
		result.removeIf(cell -> {
			for (int offset : PathFinder.NEIGHBOURS9) {
				if (level.findMob(cell + offset) != null) return true;
			}
			return false;
		});
		return result;
	}

	private void placeMoss(Level level, int cell) {
		Mob moss = new Greatmoss();
		moss.pos = cell;
		level.mobs.add(moss);
		for (int offset : PathFinder.NEIGHBOURS8) {
			int adjacent = cell + offset;
			if (level.map[adjacent] == Terrain.GRASS) Painter.set(level, adjacent, Terrain.HIGH_GRASS);
		}
	}
}
