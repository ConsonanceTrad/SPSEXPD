package pd.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.Talent;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.potions.Potion;
import pd.items.rings.Ring;
import pd.items.scrolls.Scroll;
import pd.levels.rooms.Room;
import pd.levels.rooms.special.SpsHiddenShopRoom;
import pd.levels.rooms.standard.EmptyRoom;
import pd.plants.Plant;
import pd.windows.WndLifeTradeItem;
import watabou.utils.Bundle;
import watabou.noosa.Game;
import watabou.utils.Random;
import watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Deterministic behavior checks for the legacy hidden shop and life trade. */
public final class SpsHiddenShopTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x484944454E53484FL);
		try {
			Dungeon.depth = 7;
			Dungeon.branch = 0;
			Dungeon.hero = new Hero();
			Scroll.initLabels();
			Potion.initColors();
			Ring.initGems();
			Generator.fullReset();

			TestLevel level = new TestLevel();
			Dungeon.level = level;
			SpsHiddenShopRoom room = new SpsHiddenShopRoom();
			room.set(10, 10, 15, 15);
			EmptyRoom neighbour = new EmptyRoom();
			neighbour.set(15, 10, 20, 15);
			Room.Door door = new Room.Door(15, 12);
			room.connected.put(neighbour, door);
			neighbour.connected.put(room, door);

			room.paint(level);
			check(door.type == Room.Door.Type.HIDDEN, "隐藏商店门没有隐藏");
			check(level.heaps.valueList().size() == SpsHiddenShopRoom.LIFE_ITEMS
					+ SpsHiddenShopRoom.GOLD_ITEMS, "隐藏商店商品数量错误");

			int life = 0;
			int gold = 0;
			Heap lifeHeap = null;
			for (Heap heap : level.heaps.valueList()) {
				check(heap.size() == 1, "隐藏商店商品发生重叠");
				if (heap.type == Heap.Type.FOR_LIFE) {
					life++;
					if (lifeHeap == null) lifeHeap = heap;
				} else if (heap.type == Heap.Type.FOR_SALE) {
					gold++;
				}
			}
			check(life == 6 && gold == 8, "生命商品或金币商品数量错误");

			check(level.mobs.size() == 1, "隐藏商店店主数量错误");
			Mob keeper = level.mobs.iterator().next();
			check(keeper instanceof TownNpc, "隐藏商店没有使用旧版店主");
			check(level.heaps.get(keeper.pos) == null, "隐藏商店店主与商品位置重叠");
			check(keeper.pos % level.width() > room.left && keeper.pos % level.width() < room.right
					&& keeper.pos / level.width() > room.top && keeper.pos / level.width() < room.bottom,
					"隐藏商店店主没有生成在房间内空格");
			TownNpc.Spec spec = ((TownNpc)keeper).spec();
			check(spec == TownNpc.Spec.ICE13 || spec == TownNpc.Spec.HONEY_POOOOT
					|| spec == TownNpc.Spec.SAID_BY_SUN, "隐藏商店店主类型错误");
			int pedestals = 0;
			for (int terrain : level.map) if (terrain == Terrain.PEDESTAL) pedestals++;
			check(pedestals > 0 && pedestals <= 9, "店主周围基座错误");

			Bundle heapBundle = new Bundle();
			lifeHeap.storeInBundle(heapBundle);
			Heap restoredHeap = new Heap();
			restoredHeap.restoreFromBundle(heapBundle);
			check(restoredHeap.type == Heap.Type.FOR_LIFE, "生命商品类型未写入存档");

			lifeHeap.items.clear();
			lifeHeap.drop(new TestItem());
			Dungeon.hero.pos = lifeHeap.pos;
			int oldPermanentHT = Dungeon.hero.permanentHT();
			int oldHeaps = level.heaps.valueList().size();
			check(WndLifeTradeItem.price() == 10, "普通职业生命价格错误");
			check(WndLifeTradeItem.purchase(lifeHeap), "生命商品购买失败");
			check(Dungeon.hero.permanentHT() == oldPermanentHT - 10, "永久生命没有正确扣除");
			check(level.heaps.valueList().size() == oldHeaps, "购买后没有原位补货");
			check(level.heaps.get(Dungeon.hero.pos).type == Heap.Type.FOR_SALE,
					"购买后原位置不是金币商品");
			check(Dungeon.hero.spendPermanentHT(10), "第二次生命扣除失败");
			check(!WndLifeTradeItem.canBuy(), "生命不足时仍允许购买");

			Dungeon.hero = new Hero();
			Dungeon.hero.heroClass = HeroClass.FOLLOWER;
			check(WndLifeTradeItem.price() == 8, "追随者生命价格错误");
			check(Dungeon.hero.spendPermanentHT(8), "追随者首次扣血失败");
			check(Dungeon.hero.permanentHT() == 22, "追随者永久生命扣除错误");
			Talent.initClassTalents(Dungeon.hero);
			Bundle heroBundle = new Bundle();
			Dungeon.hero.storeInBundle(heroBundle);
			check(heroBundle.getInt("htboost") == -8, "永久生命代价未写入英雄存档");

			System.out.println("SPS隐藏商店测试通过：14件商品、3类店主、隐藏门、生命交易与存档均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(32, 32);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }

		@Override
		public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
		}
	}

	private static final class TestItem extends Item {
		@Override
		public boolean doPickUp(Hero hero, int pos) {
			return true;
		}
	}

	private SpsHiddenShopTest() { }
}
