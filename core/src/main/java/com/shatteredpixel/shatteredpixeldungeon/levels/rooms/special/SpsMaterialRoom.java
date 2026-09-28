/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.DungeonBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FlashBangBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.RegrowthBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ShrapnelBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.WoollyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.buildblock.DoorBlock;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Locked SPS workshop containing the iron maker, bombs, and door blocks. */
public class SpsMaterialRoom extends SpecialRoom {

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY);

		Door entrance = entrance();
		Point statue = oppositeCorner(entrance);
		if (statue != null) Painter.set(level, statue, Terrain.STATUE);
		Painter.set(level, center(), Terrain.IRON_MAKER);

		ArrayList<Integer> floor = emptyCells(level, Terrain.EMPTY);
		Random.shuffle(floor);
		int bombs = Math.min(Random.IntRange(2, 3), floor.size());
		for (int i = 0; i < bombs; i++) level.drop(randomBomb(), floor.remove(0));
		int blocks = Math.min(3, floor.size());
		for (int i = 0; i < blocks; i++) level.drop(new DoorBlock(), floor.remove(0));

		entrance.set(Door.Type.LOCKED);
		level.addItemToSpawn(new IronKey(Dungeon.depth));
	}

	private Point oppositeCorner(Door entrance) {
		if (entrance.x == left) return new Point(right - 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
		if (entrance.x == right) return new Point(left + 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
		if (entrance.y == top) return new Point(Random.Int(2) == 0 ? left + 1 : right - 1, bottom - 1);
		if (entrance.y == bottom) return new Point(Random.Int(2) == 0 ? left + 1 : right - 1, top + 1);
		return null;
	}

	private ArrayList<Integer> emptyCells(Level level, int terrain) {
		ArrayList<Integer> result = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.map[cell] == terrain && level.heaps.get(cell) == null) result.add(cell);
			}
		}
		return result;
	}

	private static Item randomBomb() {
		switch (Random.Int(11)) {
			case 0:
			case 1:
			case 2: return new DungeonBomb();
			case 3: return new Firebomb();
			case 4: return new FrostBomb();
			case 5: return new FlashBangBomb();
			case 6: return new RegrowthBomb();
			case 7: return new HolyBomb();
			case 8: return new ShrapnelBomb();
			case 9: return new SmokeBomb();
			default: return new WoollyBomb();
		}
	}
}
