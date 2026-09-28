package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ImpShopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.DolyaSlate;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.PocketBall;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlienBag;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.fusion.NoomlinCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.challengelists.CourageChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.challengelists.PowerChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.challengelists.WisdomChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.Egg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomMonthEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.journalpages.Town;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.ActiveMrDestructo;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.FairyCard;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.Mobile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunA;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunB;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunC;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunD;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunE;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.MeleePan;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.MagicHand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.fusion.RocketMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.AlloyBowN;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.MetalBowN;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.PVCBowN;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.StoneBowN;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.WoodenBowN;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

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
			case 1:
				//SPS: 种子包已取消（绒布袋 VelvetPouch 为其替代品，用户裁决 2026-09-28）
				itemsToSpawn.add(chapterShootWeapon().identify(false));
				itemsToSpawn.add(new MeleePan());
				itemsToSpawn.add(new Pasty());
				itemsToSpawn.add(new NoomlinCrown());
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
			case 1: return new GunA();
			case 6: return new GunB();
			case 11: return new GunC();
			case 16: return new GunD();
			default: return new GunE();
		}
	}

	private static Item chapterBow() {
		switch (Dungeon.legacyDepth()) {
			case 1: return new WoodenBowN();
			case 6: return new StoneBowN();
			case 11: return new MetalBowN();
			case 16: return new AlloyBowN();
			default: return new PVCBowN();
		}
	}
}
