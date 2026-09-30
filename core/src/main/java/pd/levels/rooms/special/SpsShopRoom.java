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
import pd.items.PocketBall;
import pd.items.Stylus;
import pd.items.artifacts.AlienBag;
import pd.items.artifacts.TimekeepersHourglass;
import pd.items.artifacts.fusion.NoomlinCrown;
import pd.items.bags.PotionBandolier;
import pd.items.bags.ScrollHolder;
import pd.items.bags.MagicalHolster;
import pd.items.challengelists.CourageChallenge;
import pd.items.challengelists.PowerChallenge;
import pd.items.challengelists.WisdomChallenge;
import pd.items.eggs.Egg;
import pd.items.eggs.randomone.RandomMonthEgg;
import pd.items.food.staplefood.Pasty;
import pd.items.journalpages.Town;
import pd.items.quest.Mushroom;
import pd.items.summon.ActiveMrDestructo;
import pd.items.summon.FairyCard;
import pd.items.summon.Mobile;
import pd.items.weapon.guns.GunA;
import pd.items.weapon.guns.GunB;
import pd.items.weapon.guns.GunC;
import pd.items.weapon.guns.GunD;
import pd.items.weapon.guns.GunE;
import pd.items.weapon.melee.special.MeleePan;
import pd.items.weapon.missiles.arrows.MagicHand;
import pd.items.weapon.missiles.fusion.RocketMissile;
import pd.items.weapon.ranges.AlloyBowN;
import pd.items.weapon.ranges.MetalBowN;
import pd.items.weapon.ranges.PVCBowN;
import pd.items.weapon.ranges.StoneBowN;
import pd.items.weapon.ranges.WoodenBowN;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import render.utils.PathFinder;
import render.utils.Point;
import render.utils.Random;

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
			case 6:
				itemsToSpawn.add(new ScrollHolder());
				itemsToSpawn.add(new DolyaSlate().identify(false));
				itemsToSpawn.add(chapterShootWeapon().identify(false));
				break;
			case 11:
				itemsToSpawn.add(new PotionBandolier());
				itemsToSpawn.add(new Town().identify(false));
				itemsToSpawn.add(chapterShootWeapon().identify(false));
				break;
			case 16:
				itemsToSpawn.add(new MagicalHolster());
				itemsToSpawn.add(chapterShootWeapon().identify(false));
				break;
			case 21:
				itemsToSpawn.add(chapterShootWeapon().identify(false));
				itemsToSpawn.add(new CourageChallenge());
				itemsToSpawn.add(new PowerChallenge());
				itemsToSpawn.add(new WisdomChallenge());
				break;
		}

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
			default: itemsToSpawn.add(new PocketBall()); break;
		}
		itemsToSpawn.add(new Ankh());

		TimekeepersHourglass hourglass = Dungeon.hero == null ? null
				: Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
		if (hourglass != null) {
			int bags = 0;
			switch (Dungeon.legacyDepth()) {
				case 6: bags = (int)Math.ceil((5 - hourglass.sandBags) * 0.20f); break;
				case 11: bags = (int)Math.ceil((5 - hourglass.sandBags) * 0.25f); break;
				case 16: bags = (int)Math.ceil((5 - hourglass.sandBags) * 0.50f); break;
				case 21: bags = (int)Math.ceil((5 - hourglass.sandBags) * 0.80f); break;
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
		Mob shopkeeper = Dungeon.legacyDepth() > 20 ? new ImpShopkeeper() : new Shopkeeper();
		shopkeeper.pos = pos;
		level.mobs.add(shopkeeper);

		if (Dungeon.legacyDepth() > 20) {
			for (int offset : PathFinder.NEIGHBOURS9) {
				int cell = pos + offset;
				if (cell >= 0 && cell < level.length() && level.map[cell] == Terrain.EMPTY_SP) {
					level.map[cell] = Terrain.WATER;
				}
			}
		}
	}

	private int randomFreeInterior(Level level) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.heaps.get(cell) == null && level.findMob(cell) == null) candidates.add(cell);
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
			case 6: return new GunB();
			case 11: return new GunC();
			case 16: return new GunD();
			default: return new GunE();
		}
	}

	private static Item chapterBow() {
		switch (Dungeon.legacyDepth()) {
			case 0: return new WoodenBowN();   //SPS: 0 层为第一章（原 1 层）
			case 6: return new StoneBowN();
			case 11: return new MetalBowN();
			case 16: return new AlloyBowN();
			default: return new PVCBowN();
		}
	}
}
