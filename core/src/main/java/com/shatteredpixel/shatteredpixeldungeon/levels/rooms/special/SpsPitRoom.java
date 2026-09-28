/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** The sealed pit cache from SPS-PD 0.9.8. */
public class SpsPitRoom extends SpecialRoom {

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY);

		Door door = entrance();
		if (door == null) return;
		door.set(Door.Type.REGULAR);

		Point well;
		if (door.x == left) {
			well = new Point(right - 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
		} else if (door.x == right) {
			well = new Point(left + 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
		} else if (door.y == top) {
			well = new Point(Random.Int(2) == 0 ? left + 1 : right - 1, bottom - 1);
		} else {
			well = new Point(Random.Int(2) == 0 ? left + 1 : right - 1, top + 1);
		}
		int wellCell = level.pointToCell(well);
		level.map[wellCell] = Terrain.EMPTY_WELL;

		ArrayList<Integer> candidates = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (cell != wellCell) candidates.add(cell);
			}
		}
		Random.shuffle(candidates);
		if (candidates.size() < 2) return;

		int remains = candidates.remove(0);
		level.pitSign = candidates.remove(0);
		level.map[level.pitSign] = Terrain.SIGN;

		level.drop(new ScrollOfTeleportation(), remains).type = Heap.Type.SKELETON;
		level.drop(equipmentPrize(), remains);
		level.drop(new Ankh(), remains);
		level.drop(new Fadeleaf.Seed(), remains);
		level.drop(new Fadeleaf.Seed(), remains);
		for (int i = 0, n = Random.IntRange(1, 2); i < n; i++) {
			level.drop(extraPrize(level), remains);
		}
	}

	private static Item equipmentPrize() {
		Generator.Category category;
		switch (Random.Int(3)) {
			case 0: category = Generator.Category.RING; break;
			case 1: category = Generator.Category.ARTIFACT; break;
			default: category = Random.oneOf(Generator.Category.MELEEWEAPON, Generator.Category.ARMOR);
		}
		Item result = Generator.random(category);
		if (result == null) {
			result = Generator.randomUsingDefaults(Random.oneOf(
					Generator.Category.RING, Generator.Category.MELEEWEAPON, Generator.Category.ARMOR));
		}
		return result;
	}

	private static Item extraPrize(Level level) {
		if (Random.Int(2) != 0) {
			Item prize = level.findPrizeItem();
			if (prize != null) return prize;
		}
		return Generator.random(Random.oneOf(Generator.Category.POTION,
				Generator.Category.SCROLL, Generator.Category.FOOD, Generator.Category.GOLD));
	}
}
