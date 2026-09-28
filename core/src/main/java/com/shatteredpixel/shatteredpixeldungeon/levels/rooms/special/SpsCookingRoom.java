/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ShoppingCart;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Locked SPS kitchen with its central alchemy pot and food supplies. */
public class SpsCookingRoom extends SpecialRoom {

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY);
		Painter.fill(level, this, 2, Terrain.EMPTY_SP);

		Door entrance = entrance();
		Point pot = center();
		int potCell = level.pointToCell(pot);
		Painter.set(level, pot, Terrain.ALCHEMY);
		Blob.seed(potCell, 1, Alchemy.class, level);

		if (!Dungeon.LimitedDrops.SPS_SHOPPING_CART.dropped()) {
			ArrayList<Integer> cartCells = cellsWithoutHeaps(level, null);
			cartCells.remove((Integer)potCell);
			if (!cartCells.isEmpty()) {
				level.drop(new ShoppingCart(), Random.element(cartCells));
				Dungeon.LimitedDrops.SPS_SHOPPING_CART.drop();
			}
		}

		ArrayList<Integer> prizeCells = cellsWithoutHeaps(level, Terrain.EMPTY);
		Random.shuffle(prizeCells);
		int highFood = Math.min(Random.IntRange(1, 2), prizeCells.size());
		for (int i = 0; i < highFood; i++) level.drop(potionOrHighFood(level), prizeCells.remove(0));
		int food = Math.min(Random.IntRange(2, 3), prizeCells.size());
		for (int i = 0; i < food; i++) level.drop(potionOrFood(level), prizeCells.remove(0));

		entrance.set(Door.Type.LOCKED);
		level.addItemToSpawn(new IronKey(Dungeon.depth));
	}

	private ArrayList<Integer> cellsWithoutHeaps(Level level, Integer terrain) {
		ArrayList<Integer> result = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if ((terrain == null || level.map[cell] == terrain) && level.heaps.get(cell) == null) result.add(cell);
			}
		}
		return result;
	}

	private static Item potionOrFood(Level level) {
		Item prize = level.findPrizeItem(Potion.class);
		return prize != null ? prize : Generator.random(Generator.Category.FOOD);
	}

	private static Item potionOrHighFood(Level level) {
		Item prize = level.findPrizeItem(Potion.class);
		if (prize != null) return prize;
		return Generator.random(Generator.Category.HIGHFOOD);
	}
}
