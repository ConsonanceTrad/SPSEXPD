/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.rooms.special;

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Ankh;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.bombs.BuildBomb;
import pd.items.bombs.DungeonBomb;
import pd.items.bombs.HugeBomb;
import pd.items.weapon.missiles.ShitBall;
import pd.items.weapon.missiles.darts.PoisonDart;
import pd.items.weapon.missiles.fusion.RocketMissile;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import watabou.utils.Point;
import watabou.utils.Random;

import java.util.ArrayList;

/** Exact SPS hidden-shop inventory and life-payment layout. */
public class SpsHiddenShopRoom extends SpecialRoom {

	public static final int LIFE_ITEMS = 6;
	public static final int GOLD_ITEMS = 8;

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY_SP);

		ArrayList<Item> lifeItems = lifeItems();
		ArrayList<Item> goldItems = goldItems();
		ArrayList<Point> cells = orderedInteriorCells();
		int needed = lifeItems.size() + goldItems.size() + 1;
		if (cells.size() < needed) {
			ShatteredPixelDungeon.reportException(new IllegalStateException(
					"SPS hidden shop has " + cells.size() + " cells but needs " + needed));
			return;
		}

		int index = 0;
		for (Item item : lifeItems) {
			level.drop(item, level.pointToCell(cells.get(index++))).type = Heap.Type.FOR_LIFE;
		}
		for (Item item : goldItems) {
			level.drop(item, level.pointToCell(cells.get(index++))).type = Heap.Type.FOR_SALE;
		}

		ArrayList<Point> keeperCells = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				Point point = new Point(x, y);
				int cell = level.pointToCell(point);
				if (level.heaps.get(cell) == null && level.findMob(cell) == null) {
					keeperCells.add(point);
				}
			}
		}
		if (keeperCells.isEmpty()) {
			ShatteredPixelDungeon.reportException(new IllegalStateException(
					"SPS hidden shop has no free cell for its keeper"));
			return;
		}
		Point keeperCell = Random.element(keeperCells);
		TownNpc.Spec[] keepers = {TownNpc.Spec.ICE13, TownNpc.Spec.HONEY_POOOOT,
				TownNpc.Spec.SAID_BY_SUN};
		TownNpc keeper = new TownNpc().configure(Random.element(keepers));
		keeper.pos = level.pointToCell(keeperCell);
		level.mobs.add(keeper);
		paintPedestal(level, keeperCell);

		for (Door door : connected.values()) door.set(Door.Type.HIDDEN);
	}

	private static ArrayList<Item> lifeItems() {
		ArrayList<Item> result = new ArrayList<>();
		result.add(lifeEquipment(Generator.Category.MELEEWEAPON));
		result.add(lifeEquipment(Generator.Category.ARMOR));
		result.add(lifeEquipment(Generator.Category.WAND));
		result.add(lifeEquipment(Generator.Category.RING));
		result.add(Generator.random(Generator.Category.ARTIFACT));
		result.add(new Ankh());
		return result;
	}

	private static Item lifeEquipment(Generator.Category category) {
		return Generator.random(category).identify(false).uncurse().upgrade(Dungeon.legacyDepth());
	}

	private static ArrayList<Item> goldItems() {
		ArrayList<Item> result = new ArrayList<>();
		result.add(Generator.random(Generator.Category.POTION));
		result.add(Generator.random(Generator.Category.SCROLL));
		result.add(linkDrop());
		result.add(saleEquipment(Generator.Category.MELEEWEAPON));
		result.add(saleEquipment(Generator.Category.ARMOR));
		result.add(saleEquipment(Generator.Category.WAND));
		result.add(saleEquipment(Generator.Category.RING));
		result.add(Generator.random(Generator.Category.ARTIFACT));
		return result;
	}

	private static Item saleEquipment(Generator.Category category) {
		return Generator.random(category).uncurse().upgrade(Dungeon.legacyDepth());
	}

	private static Item linkDrop() {
		Class<? extends Item>[] classes = new Class[]{BuildBomb.class, DungeonBomb.class,
				HugeBomb.class, RocketMissile.class, PoisonDart.class, ShitBall.class};
		float[] weights = {3, 1, 1, 1, 2, 2};
		return Generator.random(classes[Random.chances(weights)]);
	}

	private ArrayList<Point> orderedInteriorCells() {
		ArrayList<Point> perimeter = new ArrayList<>();
		for (int x = left + 1; x < right; x++) perimeter.add(new Point(x, top + 1));
		for (int y = top + 2; y < bottom; y++) perimeter.add(new Point(right - 1, y));
		for (int x = right - 2; x > left; x--) perimeter.add(new Point(x, bottom - 1));
		for (int y = bottom - 2; y > top + 1; y--) perimeter.add(new Point(left + 1, y));

		Point inset = pointInside(entrance(), 1);
		int entranceIndex = perimeter.indexOf(inset);
		if (entranceIndex < 0) entranceIndex = 0;
		int start = Math.floorMod(entranceIndex + (perimeter.size() - LIFE_ITEMS - GOLD_ITEMS) / 2,
				perimeter.size());

		ArrayList<Point> result = new ArrayList<>();
		for (int i = 0; i < perimeter.size(); i++) {
			result.add(perimeter.get((start + i) % perimeter.size()));
		}
		for (int y = top + 2; y < bottom - 1; y++) {
			for (int x = left + 2; x < right - 1; x++) result.add(new Point(x, y));
		}
		return result;
	}

	private void paintPedestal(Level level, Point center) {
		for (int y = center.y - 1; y <= center.y + 1; y++) {
			for (int x = center.x - 1; x <= center.x + 1; x++) {
				if (x <= left || x >= right || y <= top || y >= bottom) continue;
				int cell = level.pointToCell(new Point(x, y));
				if (level.map[cell] == Terrain.EMPTY_SP) Level.set(cell, Terrain.PEDESTAL, level);
			}
		}
	}
}
