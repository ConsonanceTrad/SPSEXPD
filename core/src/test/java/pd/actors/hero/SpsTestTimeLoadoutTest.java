package pd.actors.hero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Challenges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.items.ChallengeBook;
import pd.items.DolyaSlate;
import pd.items.Elevator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.Palantir;
import pd.items.PocketBall;
import pd.items.PowerHand;
import pd.items.SaveYourLife;
import pd.items.SkillBook;
import pd.items.SoulCollect;
import pd.items.TomeOfMastery;
import pd.items.artifacts.MasterThievesArmband;
import pd.items.bags.MagicalHolster;
import pd.items.bags.PotionBandolier;
import pd.items.bags.ScrollHolder;
import pd.items.bags.ShoppingCart;
import pd.items.eggs.AflyEgg;
import pd.items.eggs.EasterEgg;
import pd.items.eggs.GoldDragonEgg;
import pd.items.eggs.randomone.RandomMonthEgg;
import pd.items.food.Honey;
import pd.items.food.completefood.Hamburger;
import pd.items.food.completefood.MoonCake;
import pd.items.misc.FourClover;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.items.potions.PotionOfMending;
import pd.items.potions.PotionOfMindVision;
import pd.items.quest.AdventureJournal;
import pd.items.quest.ChallengeJournal;
import pd.items.rings.Ring;
import pd.items.rings.RingOfAccuracy;
import pd.items.rings.RingOfElements;
import pd.items.rings.RingOfEnergy;
import pd.items.rings.RingOfEvasion;
import pd.items.rings.RingOfForce;
import pd.items.rings.RingOfFuror;
import pd.items.rings.RingOfHaste;
import pd.items.rings.RingOfMight;
import pd.items.rings.RingOfSharpshooting;
import pd.items.rings.RingOfTenacity;
import pd.items.rings.fusion.RingOfKnowledge;
import pd.items.rings.fusion.RingOfMagic;
import pd.items.scrolls.ScrollOfDummy;
import pd.items.scrolls.ScrollOfIdentify;
import pd.items.scrolls.ScrollOfMagicMapping;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.items.wands.WandOfMagicMissile;
import pd.items.weapon.melee.special.TestWeapon;
import pd.items.weapon.missiles.ThrowingKnife;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Dewcatcher;
import pd.plants.Plant;
import pd.plants.Seedpod;
import render.noosa.Game;
import render.utils.data.SparseArray;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsTestTimeLoadoutTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		pd.items.scrolls.Scroll.initLabels();
		pd.items.potions.Potion.initColors();
		Ring.initGems();
		try {
			testChallengeGate();
			testCompleteLoadout();
			testLegacyContainersAndTome();
			testDummyMechanicsAndEdgeSafety();
			testBilingualResources();
			System.out.println("SPS测试模式开局通过：充满的多利亚石板、25条异界路线、8条挑战路线、完整物资数量、十二枚+10戒指、20000金币/10000生命及玩偶机制均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			Dungeon.challenges = 0;
		}
	}

	private static void testChallengeGate() {
		Hero hero = freshHero(0);
		Dungeon.gold = 77;
		SpsTestTimeLoadout.apply(hero);
		check(hero.belongings.getItem(Elevator.class) == null && Dungeon.gold == 77,
				"未启用TEST_TIME时仍发放了测试物资");
		//SPS: 主背包基准 40 格（5x8）；该断言防止 TEST_TIME 的扩容污染普通模式
		check(hero.belongings.backpack.capacity() == Belongings.BACKPACK_CAPACITY,
				"普通模式背包容量被测试模式污染");
	}

	private static void testCompleteLoadout() {
		Hero hero = freshHero(Challenges.TEST_TIME);
		Dungeon.depth = 17;
		Dungeon.branch = 42;
		SpsTestTimeLoadout.apply(hero);

		AdventureJournal adventures = hero.belongings.getItem(AdventureJournal.class);
		ChallengeJournal challenges = hero.belongings.getItem(ChallengeJournal.class);
		for (int i = 0; i < AdventureJournal.DESTINATION_COUNT; i++) {
			check(adventures != null && adventures.isUnlocked(i), "测试模式未解锁异界路线" + i);
		}
		check(adventures instanceof DolyaSlate && adventures.charge() == AdventureJournal.FULL_CHARGE,
				"测试模式没有获得充满的多利亚石板");
		for (int i = 0; i < ChallengeJournal.CHALLENGE_COUNT; i++) {
			check(challenges != null && challenges.isUnlocked(i), "测试模式未解锁挑战路线" + i);
		}
		check(challenges instanceof ChallengeBook, "测试模式没有获得ChallengeBook实体");

		Class<?>[] uniqueItems = {
				Elevator.class, SkillBook.class, ScrollHolder.class,
				PotionBandolier.class, ShoppingCart.class, MagicalHolster.class, Palantir.class,
				SoulCollect.class, PowerHand.class, TomeOfMastery.class, TestWeapon.class,
				EasterEgg.class, AflyEgg.class, GoldDragonEgg.class, SaveYourLife.class,
				FourClover.class, MasterThievesArmband.class
		};
		for (Class<?> type : uniqueItems) check(countExact(hero, type) == 1, "测试物资缺失或重复：" + type.getSimpleName());

		check(countExact(hero, PocketBall.class) == 10, "精灵球数量不是10");
		check(countExact(hero, ScrollOfIdentify.class) == 199, "鉴定卷轴数量不是199");
		check(countExact(hero, ScrollOfMagicMapping.class) == 199, "地图卷轴数量不是199");
		check(countExact(hero, MoonCake.class) == 199, "月饼数量不是199");
		check(countExact(hero, PotionOfMindVision.class) == 199, "灵视药剂数量不是199");
		Class<?>[] nornStones = {YellowNornStone.class, BlueNornStone.class, OrangeNornStone.class,
				PurpleNornStone.class, GreenNornStone.class};
		for (Class<?> type : nornStones) check(countExact(hero, type) == 199, type.getSimpleName() + "数量不是199");
		Class<?>[] tens = {Seedpod.Seed.class, Dewcatcher.Seed.class, ScrollOfDummy.class,
				PotionOfMending.class, ScrollOfPsionicBlast.class, Hamburger.class,
				RandomMonthEgg.class, Honey.class};
		for (Class<?> type : tens) check(countExact(hero, type) == 10, type.getSimpleName() + "数量不是10");

		Class<?>[] ringTypes = {RingOfElements.class, RingOfAccuracy.class, RingOfMight.class,
				RingOfForce.class, RingOfFuror.class, RingOfEvasion.class, RingOfEnergy.class,
				RingOfMagic.class, RingOfHaste.class, RingOfSharpshooting.class,
				RingOfTenacity.class, RingOfKnowledge.class};
		for (Class<?> type : ringTypes) {
			Ring ring = (Ring)findExact(hero, type);
			check(ring != null && ring.level() == 10 && ring.isIdentified(), type.getSimpleName() + "不是已鉴定+10戒指");
		}
		MasterThievesArmband armband = hero.belongings.getItem(MasterThievesArmband.class);
		check(armband != null && armband.level() == 5, "盗贼袖章不是+5");
		check(Dungeon.gold == 20000 && hero.HT == 10000 && hero.HP == 10000,
				"测试模式金币或最大生命没有恢复到规定值（金币20000、生命10000）");
		check(Dungeon.depth == 1 && Dungeon.branch == 0, "测试模式没有保持主线第一层开局");
		check(hero.belongings.backpack.capacity() >= 64, "测试模式背包容量不足64格");
	}

	private static void testLegacyContainersAndTome() {
		//SPS: 种子包已取消（绒布袋 VelvetPouch 为其替代品），相关断言移除

		MagicalHolster holster = new MagicalHolster();
		//SPS: 法器包与魔法套筒已合并（取大：容量 30、价值 60）
		check(holster.capacity() == 34 && holster.value() == 60, "魔法套筒容量或价值错误");
		check(holster.canHold(new WandOfMagicMissile()), "魔法套筒没有收纳法杖");
		check(!holster.canHold(new Seedpod.Seed()), "魔法套筒错误收纳种子");
		//SPS: 投掷武器统一存放暗器袋（用户裁决 2026-09-28）
		check(!holster.canHold(new ThrowingKnife()), "魔法套筒错误收纳投掷武器");

		TomeOfMastery tome = new TomeOfMastery();
		check(tome.actions(new Hero()).contains(TomeOfMastery.AC_READ)
				&& TomeOfMastery.TIME_TO_READ == 10f, "精通之书阅读动作或耗时错误");
	}

	private static void testDummyMechanicsAndEdgeSafety() throws Exception {
		Actor.clear();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Hero hero = freshHero(Challenges.TEST_TIME);
		hero.pos = 0;
		Method spawn = ScrollOfDummy.class.getDeclaredMethod("spawnDummy", int.class, int.class);
		spawn.setAccessible(true);
		ScrollOfDummy.MiniDummy dummy = (ScrollOfDummy.MiniDummy)spawn.invoke(null, 0, 30);
		check(dummy != null && dummy.HT == 30 && dummy.HP == 30 && dummy.pos >= 0
				&& dummy.pos < level.length(), "地图边缘没有安全生成30生命玩偶");
		check(level.mobs().contains(dummy), "生成的玩偶没有加入地图");
		dummy.damage(99, hero);
		check(dummy.HP == 28, "玩偶没有把正伤害固定为2");
		dummy.defenseProc(hero, 7);
		check(dummy.HP == 29, "玩偶受击时没有先恢复1点生命");
		Method decay = ScrollOfDummy.MiniDummy.class.getDeclaredMethod("decay");
		decay.setAccessible(true);
		decay.invoke(dummy);
		check(dummy.HP == 27, "玩偶每回合没有按旧版规则衰减");
		ScrollOfDummy.MiniDummy empowered = (ScrollOfDummy.MiniDummy)spawn.invoke(null, 0, 50);
		check(empowered != null && empowered.HT == 50 && empowered.HP == 50, "强化阅读没有生成50生命玩偶");
	}

	private static void testBilingualResources() throws Exception {
		String zh = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		String[] keys = {"items.bags.wandholster.name=",
				"items.tomeofmastery.name=", "items.scrolls.scrollofdummy.name=",
				"items.scrolls.scrollofdummy$minidummy.name="};
		for (String key : keys) check(zh.contains(key) && en.contains(key), "中英文资源缺少键：" + key);
		check(zh.contains("精通之书") && zh.contains("吵闹玩偶") && !zh.contains("�"), "测试物品中文乱码或缺失");
	}

	private static Hero freshHero(int challenges) {
		Dungeon.challenges = challenges;
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.HP = 0;
		Dungeon.hero = hero;
		return hero;
	}

	private static int countExact(Hero hero, Class<?> type) {
		int count = 0;
		for (Item item : hero.belongings) if (item.getClass() == type) count += item.quantity();
		return count;
	}

	private static Item findExact(Hero hero, Class<?> type) {
		for (Item item : hero.belongings) if (item.getClass() == type) return item;
		return null;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<Trap>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
			Arrays.fill(heroFOV, true);
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) { heap = new Heap(); heap.pos = cell; heaps.put(cell, heap); }
			heap.drop(item);
			return heap;
		}
	}

	private SpsTestTimeLoadoutTest() { }
}
