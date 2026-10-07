/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.rooms.special;

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Ankh;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.bombs.BuildBomb;
import pd.items.equipment.bombs.DungeonBomb;
import pd.items.equipment.bombs.HugeBomb;
import pd.items.equipment.weapon.missiles.ShitBall;
import pd.items.equipment.weapon.missiles.darts.PoisonDart;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import render.utils.geom.Point;
import render.utils.math.Random;

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
			//SPSXPD: 秘密商店一律以生命上限交易
			level.drop(item, level.pointToCell(cells.get(index++))).type = Heap.Type.FOR_LIFE;
		}

		ArrayList<Point> keeperCells = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				Point point = new Point(x, y);
				int cell = level.pointToCell(point);
				if (level.heaps.get(cell) == null && level.mobs().findMob(cell) == null) {
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
		pd.actors.mobs.npcs.SpsHiddenShopKeeper keeper = new pd.actors.mobs.npcs.SpsHiddenShopKeeper().configure(Random.element(keepers));
		//SPSXPD: 店主负责在货架见底时补货（补的也是生命货物）
		keeper.shopRoom = this;
		keeper.pos = level.pointToCell(keeperCell);
		//SPSXPD: 商店商人守摊不动（configure 会按 Spec 设为 WANDERING）
		keeper.state = keeper.PASSIVE;
		level.mobs().add(keeper);
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
				HugeBomb.class, PoisonDart.class, ShitBall.class};
		float[] weights = {3, 1, 1, 2, 2};
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

	/** SPSXPD: 秘密商店卖到少于这个件数就补货（与普通商店同一阈值）。 */
	public static final int RESTOCK_THRESHOLD = 4;

	/** SPSXPD: 数本层还剩几堆生命货物，不足阈值就补一批。 */
	public void checkRestock() {
		if (Dungeon.level == null) return;

		int forLife = 0;
		for (Heap h : Dungeon.level.heaps.valueList()) {
			if (h.type == Heap.Type.FOR_LIFE) forLife++;
		}
		if (forLife >= RESTOCK_THRESHOLD) return;

		restock();
	}

	/** SPSXPD: 补一批生命货物（沿用原物品构成），位置随机；补货同样是生命代价。 */
	public void restock() {
		if (Dungeon.level == null) return;

		ArrayList<Item> fresh = new ArrayList<>();
		fresh.addAll(lifeItems());
		fresh.addAll(goldItems());
		for (Item item : fresh) {
			int cell = randomFreeInterior(Dungeon.level);
			if (cell == -1) break;
			Dungeon.level.drop(item, cell).type = Heap.Type.FOR_LIFE;
		}
	}

	private int randomFreeInterior(Level level) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.heaps.get(cell) == null && level.mobs().findMob(cell) == null) candidates.add(cell);
			}
		}
		return candidates.isEmpty() ? -1 : Random.element(candidates);
	}
}
