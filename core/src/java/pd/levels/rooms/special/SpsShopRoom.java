package pd.levels.rooms.special;

import pd.Dungeon;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.ImpShopkeeper;
import pd.actors.mobs.npcs.Shopkeeper;
import pd.items.Ankh;
import pd.items.DolyaSlate;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Honeypot;
import pd.items.Item;
import pd.items.Stylus;
import pd.items.equipment.artifacts.AlienBag;
import pd.items.equipment.artifacts.TimekeepersHourglass;
import pd.items.equipment.artifacts.fusion.NoomlinCrown;
import pd.items.equipment.bags.MagicalHolster;
import pd.items.equipment.bags.PotionBandolier;
import pd.items.equipment.bags.ScrollHolder;
import pd.items.specific.challengelists.CourageChallenge;
import pd.items.specific.challengelists.PowerChallenge;
import pd.items.specific.challengelists.WisdomChallenge;
import pd.items.consum.eggs.Egg;
import pd.items.consum.eggs.randomone.RandomMonthEgg;
import pd.items.consum.food.staplefood.Pasty;
import pd.items.specific.journalpages.Town;
import pd.items.quest.Mushroom;
import pd.items.summon.ActiveMrDestructo;
import pd.items.summon.FairyCard;
import pd.items.summon.Mobile;
import pd.items.equipment.weapon.guns.GunA;
import pd.items.equipment.weapon.guns.GunB;
import pd.items.equipment.weapon.guns.GunC;
import pd.items.equipment.weapon.guns.GunD;
import pd.items.equipment.weapon.guns.GunE;
import pd.items.equipment.weapon.melee.special.MeleePan;
import pd.items.equipment.weapon.missiles.arrows.MagicHand;
import pd.items.equipment.weapon.missiles.fusion.RocketMissile;
import pd.items.equipment.weapon.ranges.AlloyBowN;
import pd.items.equipment.weapon.ranges.MetalBowN;
import pd.items.equipment.weapon.ranges.PVCBowN;
import pd.items.equipment.weapon.ranges.StoneBowN;
import pd.items.equipment.weapon.ranges.WoodenBowN;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import pd.mechanics.pathfind.PathFinder;
import render.utils.geom.Point;
import render.utils.math.Random;

import java.util.ArrayList;
import java.util.Collections;

/** The chapter-transition shop from SPS-PD 0.9.8. */
public class SpsShopRoom extends ShopRoom {

	@Override
	public int minWidth() {
		return 8;
	}

	@Override
	public int minHeight() {
		return 8;
	}

	@Override
	public int maxConnections(int direction) {
		return direction == ALL ? 16 : 4;
	}

	@Override
	public int spacesNeeded() {
		ensureItems();
		return itemsToSpawn.size() + 1;
	}

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY_SP);

		ensureItems();
		placeLegacyItems(level);
		placeShopkeeper(level);

		for (Door door : connected.values()) door.set(Door.Type.REGULAR);
		itemsToSpawn = null;
	}

	private void ensureItems() {
		if (itemsToSpawn != null) return;
		itemsToSpawn = new ArrayList<>();

		switch (Dungeon.legacyDepth()) {
			case 0:
				//SPS: 0 层特殊初始层商店；种子包已取消（绒布袋 VelvetPouch 为其替代品，用户裁决 2026-09-28）
				itemsToSpawn.add(chapterShootWeapon().identify(false));
				itemsToSpawn.add(new MeleePan());
				itemsToSpawn.add(new Pasty());
				itemsToSpawn.add(new NoomlinCrown());
				//SPS: 任务蘑菇固定出售，售价 10 金币（配合开局 10 金币）
				itemsToSpawn.add(new Mushroom());
				break;
			case 8:
				itemsToSpawn.add(new ScrollHolder());
				itemsToSpawn.add(new DolyaSlate().identify(false));
				itemsToSpawn.add(chapterShootWeapon().identify(false));
				break;
			case 16:
				itemsToSpawn.add(new PotionBandolier());
				itemsToSpawn.add(new Town().identify(false));
				itemsToSpawn.add(chapterShootWeapon().identify(false));
				break;
			case 24:
				itemsToSpawn.add(new MagicalHolster());
				itemsToSpawn.add(chapterShootWeapon().identify(false));
				break;
			case 32:
				itemsToSpawn.add(chapterShootWeapon().identify(false));
				itemsToSpawn.add(new CourageChallenge());
				itemsToSpawn.add(new PowerChallenge());
				itemsToSpawn.add(new WisdomChallenge());
				break;
		}

		//SPS: switch 里按 depth 固定列出的任务/特殊物品不参与补货（见 addCommonItems）
		addCommonItems(itemsToSpawn);
	}

	/**
	 * SPS: 通用货物（药水/卷轴/武器/护甲/稀有等）。
	 * 首次铺货与补货共用同一段；补货只重跑这里，所以露珠菌孢这类固定任务物品不会被补出来。
	 */
	private void addCommonItems(ArrayList<Item> itemsToSpawn) {

		itemsToSpawn.add(Generator.random(Generator.Category.POTION));
		itemsToSpawn.add(Generator.random(Generator.Category.POTION));
		itemsToSpawn.add(Generator.random(Generator.Category.SCROLL));
		itemsToSpawn.add(Generator.random(Generator.Category.SCROLL));
		itemsToSpawn.add(new MagicHand(5));
		itemsToSpawn.add(AlienBag.randomBombSupply());
		itemsToSpawn.add(Random.Int(2) == 0
				? Generator.random(Generator.Category.POTION)
				: Generator.random(Generator.Category.SCROLL));
		itemsToSpawn.add(Generator.random(Generator.Category.RANGEWEAPON));
		itemsToSpawn.add(Generator.random(Generator.Category.MELEEWEAPON));
		itemsToSpawn.add(Generator.random(Generator.Category.ARMOR));

		if (Random.Int(3) == 0) {
			itemsToSpawn.add(Random.Int(2) == 0 ? new RandomMonthEgg() : new Egg());
		}
		itemsToSpawn.add(new RocketMissile());
		switch (Random.Int(6)) {
			case 1: itemsToSpawn.add(new ActiveMrDestructo()); break;
			case 2: itemsToSpawn.add(new FairyCard()); break;
			case 3: itemsToSpawn.add(new Mobile()); break;
			case 4: itemsToSpawn.add(new Honeypot()); break;
			default: itemsToSpawn.add(new Mobile()); break;
		}
		itemsToSpawn.add(new Ankh());

		TimekeepersHourglass hourglass = Dungeon.hero == null ? null
				: Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
		if (hourglass != null) {
			int bags = 0;
			switch (Dungeon.legacyDepth()) {
				case 8: bags = (int)Math.ceil((5 - hourglass.sandBags) * 0.20f); break;
				case 16: bags = (int)Math.ceil((5 - hourglass.sandBags) * 0.25f); break;
				case 24: bags = (int)Math.ceil((5 - hourglass.sandBags) * 0.50f); break;
				case 32: bags = (int)Math.ceil((5 - hourglass.sandBags) * 0.80f); break;
			}
			for (int i = 0; i < bags; i++) {
				itemsToSpawn.add(new TimekeepersHourglass.sandBag());
				hourglass.sandBags++;
			}
		}

		Item rare;
		switch (Random.Int(4)) {
			case 0:
				rare = Generator.random(Generator.Category.WAND);
				if (rare != null) rare.level(0);
				break;
			case 1:
				rare = Generator.random(Generator.Category.RING);
				if (rare != null) rare.level(1);
				break;
			case 2:
				rare = Generator.random(Generator.Category.ARTIFACT);
				break;
			default:
				rare = new Stylus();
		}
		if (rare == null) rare = new Stylus();
		rare.identify(false);
		rare.cursed = false;
		rare.cursedKnown = false;
		itemsToSpawn.add(rare);

		if (itemsToSpawn.size() > 39) {
			throw new IllegalStateException("Shop attempted to carry more than 39 items");
		}
		Collections.shuffle(itemsToSpawn);
	}

	private void placeLegacyItems(Level level) {
		int passageWidth = width() - 3;
		int passageHeight = height() - 3;
		int perimeter = passageWidth * 2 + passageHeight * 2;
		int position = perimeterPosition(entrance(), passageWidth, passageHeight)
				+ (perimeter - itemsToSpawn.size()) / 2;

		for (Item item : itemsToSpawn) {
			Point point = perimeterPoint((position + perimeter) % perimeter, passageWidth, passageHeight);
			int cell = level.pointToCell(point);
			if (level.heaps.get(cell) != null) {
				cell = randomFreeInterior(level);
				if (cell == -1) throw new IllegalStateException("No free cell for SPS shop stock");
			}
			level.drop(item, cell).type = Heap.Type.FOR_SALE;
			position++;
		}
	}

	@Override
	protected void placeShopkeeper(Level level) {
		int pos = randomFreeInterior(level);
		if (pos == -1) pos = level.pointToCell(center());
		Mob shopkeeper = Dungeon.legacyDepth() > 31 ? new ImpShopkeeper() : new Shopkeeper();
		shopkeeper.pos = pos;

		//SPS: 新一层的商店要重置涨价倍率与免费开关（这两个是静态的，属于"本层这家店"），
		//并把房间交给商人，卖光后由它触发补货。
		Shopkeeper.priceMultiplier = 1f;
		Shopkeeper.freeAndNoRestock = false;
		((Shopkeeper) shopkeeper).shopRoom = this;

		level.mobs().add(shopkeeper);

		if (Dungeon.legacyDepth() > 31) {
			for (int offset : PathFinder.NEIGHBOURS9) {
				int cell = pos + offset;
				if (cell >= 0 && cell < level.length() && level.map[cell] == Terrain.EMPTY_SP) {
					level.map[cell] = Terrain.WATER;
				}
			}
		}
	}

	/** SPS: 本层待售堆少于这个数就补货。 */
	private static final int RESTOCK_THRESHOLD = 4;

	/** SPS: 统计本层还剩几个待售堆，不足阈值就补货（免费后不再补）。商人被打时也会调一次。 */
	public void checkRestock() {
		if (Shopkeeper.freeAndNoRestock || Dungeon.level == null) return;

		int forSale = 0;
		for (Heap h : Dungeon.level.heaps.valueList()) {
			if (h.type == Heap.Type.FOR_SALE) forSale++;
		}
		if (forSale >= RESTOCK_THRESHOLD) return;

		restock();
	}

	/**
	 * SPS: 补一批通用货物（不含固定任务/特殊物品），摆在店内空位上。
	 * 等于把通用货补满一轮，位置重新随机。
	 */
	public void restock() {
		if (Shopkeeper.freeAndNoRestock || Dungeon.level == null) return;

		ArrayList<Item> fresh = new ArrayList<>();
		addCommonItems(fresh);
		if (fresh.isEmpty()) return;

		for (Item item : fresh) {
			int cell = randomFreeInterior(Dungeon.level);
			if (cell == -1) break;
			Dungeon.level.drop(item, cell).type = Heap.Type.FOR_SALE;
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

	private int perimeterPosition(Point point, int passageWidth, int passageHeight) {
		if (point.y == top) return point.x - left - 1;
		if (point.x == right) return point.y - top - 1 + passageWidth;
		if (point.y == bottom) return right - point.x - 1 + passageWidth + passageHeight;
		return point.y == top + 1 ? 0 : bottom - point.y - 1 + passageWidth * 2 + passageHeight;
	}

	private Point perimeterPoint(int position, int passageWidth, int passageHeight) {
		if (position < passageWidth) {
			return new Point(left + 1 + position, top + 1);
		} else if (position < passageWidth + passageHeight) {
			return new Point(right - 1, top + 1 + position - passageWidth);
		} else if (position < passageWidth * 2 + passageHeight) {
			return new Point(right - 1 - (position - passageWidth - passageHeight), bottom - 1);
		} else {
			return new Point(left + 1, bottom - 1 - (position - passageWidth * 2 - passageHeight));
		}
	}

	private static Item chapterShootWeapon() {
		if (Random.Int(2) == 0) return chapterBow();
		switch (Dungeon.legacyDepth()) {
			case 0: return new GunA();   //SPS: 0 层为第一章（原 1 层）
			case 8: return new GunB();
			case 16: return new GunC();
			case 24: return new GunD();
			default: return new GunE();
		}
	}

	private static Item chapterBow() {
		switch (Dungeon.legacyDepth()) {
			case 0: return new WoodenBowN();   //SPS: 0 层为第一章（原 1 层）
			case 8: return new StoneBowN();
			case 16: return new MetalBowN();
			case 24: return new AlloyBowN();
			default: return new PVCBowN();
		}
	}
}
