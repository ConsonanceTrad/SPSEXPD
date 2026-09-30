/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.rooms.special;

import pd.Dungeon;
import pd.items.Dewdrop;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.RedDewdrop;
import pd.items.VioletDewdrop;
import pd.items.YellowDewdrop;
import pd.items.bags.ShoppingCart;
import pd.items.eggs.AflyEgg;
import pd.items.eggs.EasterEgg;
import pd.items.eggs.Egg;
import pd.items.eggs.YearPetEgg;
import pd.items.food.AflyFood;
import pd.items.food.MeatPie;
import pd.items.food.Pasty;
import pd.items.food.SupplyRation;
import pd.items.keys.IronKey;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import pd.mechanics.pathfind.PathFinder;
import render.utils.Point;
import render.utils.Random;

import java.util.ArrayList;

/** Locked SPS ruin containing four distinct themed caches and an iron maker. */
public class SpsRuinRoom extends SpecialRoom {

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY_DECO);

		Door door = entrance();
		door.set(Door.Type.LOCKED);
		level.addItemToSpawn(new IronKey(Dungeon.depth));
		carveRuins(level, door);

		int center = level.pointToCell(center());
		if (!Dungeon.LimitedDrops.SPS_SHOPPING_CART.dropped()) {
			ArrayList<Integer> cartCells = freeInterior(level);
			cartCells.remove((Integer)center);
			if (!cartCells.isEmpty()) {
				level.drop(new ShoppingCart(), Random.element(cartCells));
				Dungeon.LimitedDrops.SPS_SHOPPING_CART.drop();
			}
		}

		level.drop(primaryPrize(), center).type = Heap.Type.CHEST;
		ArrayList<Integer> webCells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (level.insideMap(cell) && level.heaps.get(cell) == null) webCells.add(cell);
		}
		if (!webCells.isEmpty()) level.drop(foodPrize(), Random.element(webCells)).type = Heap.Type.M_WEB;

		ArrayList<Integer> free = freeInterior(level);
		Random.shuffle(free);
		if (!free.isEmpty()) level.drop(equipmentPrize(), free.remove(0)).type = Heap.Type.REMAINS;
		if (!free.isEmpty()) level.drop(growthPrize(), free.remove(0)).type = Heap.Type.E_DUST;

		Point maker = oppositeCorner(door);
		if (maker != null) Painter.set(level, maker, Terrain.IRON_MAKER);
	}

	private void carveRuins(Level level, Door door) {
		if (door.x == left || door.x == right) {
			for (int y = top + 1; y < bottom; y++) {
				Painter.drawInside(level, this, new Point(door.x, y),
						Random.IntRange(1, Math.max(1, width() - 2)), Terrain.EMPTY_SP);
			}
		} else {
			for (int x = left + 1; x < right; x++) {
				Painter.drawInside(level, this, new Point(x, door.y),
						Random.IntRange(1, Math.max(1, height() - 2)), Terrain.EMPTY_SP);
			}
		}
	}

	private Point oppositeCorner(Door entrance) {
		if (entrance.x == left) {
			return new Point(right - 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
		}
		if (entrance.x == right) return new Point(left + 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
		if (entrance.y == top) return new Point(Random.Int(2) == 0 ? left + 1 : right - 1, bottom - 1);
		if (entrance.y == bottom) return new Point(Random.Int(2) == 0 ? left + 1 : right - 1, top + 1);
		return null;
	}

	private ArrayList<Integer> freeInterior(Level level) {
		ArrayList<Integer> result = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.heaps.get(cell) == null) result.add(cell);
			}
		}
		return result;
	}

	private static Item primaryPrize() {
		switch (Random.Int(3)) {
			case 0: return Random.oneOf(new Egg(), new EasterEgg(), new AflyEgg(), new YearPetEgg());
			case 1: return Generator.random(Generator.Category.RING);
			default: return Generator.random(Generator.Category.ARTIFACT);
		}
	}

	private static Item foodPrize() {
		switch (Random.Int(3)) {
			case 0: return Generator.random(Generator.Category.FOOD);
			case 1: return Generator.random(Generator.Category.HIGHFOOD);
			default: return Generator.random(Generator.Category.MEDICINE);
		}
	}

	private static Item equipmentPrize() {
		return Generator.random(Random.oneOf(Generator.Category.MELEEWEAPON,
				Generator.Category.ARMOR, Generator.Category.WAND));
	}

	private static Item growthPrize() {
		switch (Random.Int(3)) {
			case 0:
				switch (Random.Int(21)) {
					case 0: case 1: case 2: case 3: return new Dewdrop();
					case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: return new YellowDewdrop();
					case 12: case 13: case 14: case 15: case 16: return new RedDewdrop();
					case 17: case 18: case 19: return new VioletDewdrop();
					default: return Generator.random(Generator.Category.SEED);
				}
			case 1: return Generator.random(Generator.Category.SEED);
			default: return Random.oneOf(new GreenNornStone(), new BlueNornStone(), new OrangeNornStone(),
					new PurpleNornStone(), new YellowNornStone());
		}
	}
}
