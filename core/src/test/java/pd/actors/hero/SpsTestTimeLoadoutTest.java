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
import pd.items.PowerHand;
import pd.items.SaveYourLife;
import pd.items.SkillBook;
import pd.items.SoulCollect;
import pd.items.TomeOfMastery;
import pd.items.equipment.artifacts.MasterThievesArmband;
import pd.items.equipment.bags.BambooBasket;
import pd.items.equipment.bags.MagicalHolster;
import pd.items.equipment.bags.PotionBandolier;
import pd.items.equipment.bags.ScrollHolder;
import pd.items.equipment.bags.ShoppingCart;
import pd.items.consum.eggs.AflyEgg;
import pd.items.consum.eggs.EasterEgg;
import pd.items.consum.eggs.GoldDragonEgg;
import pd.items.consum.eggs.randomone.RandomMonthEgg;
import pd.items.consum.food.Honey;
import pd.items.consum.food.completefood.Hamburger;
import pd.items.consum.food.completefood.MoonCake;
import pd.items.misc.FourClover;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.potions.PotionOfMindVision;
import pd.items.consum.potions.elixirs.WishPotion;
import pd.items.quest.AdventureJournal;
import pd.items.quest.ChallengeJournal;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.rings.RingOfAccuracy;
import pd.items.equipment.rings.RingOfElements;
import pd.items.equipment.rings.RingOfEnergy;
import pd.items.equipment.rings.RingOfEvasion;
import pd.items.equipment.rings.RingOfForce;
import pd.items.equipment.rings.RingOfFuror;
import pd.items.equipment.rings.RingOfHaste;
import pd.items.equipment.rings.RingOfMight;
import pd.items.equipment.rings.RingOfSharpshooting;
import pd.items.equipment.rings.RingOfTenacity;
import pd.items.equipment.rings.fusion.RingOfKnowledge;
import pd.items.equipment.rings.fusion.RingOfMagic;
import pd.items.consum.scrolls.ScrollOfDummy;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.items.equipment.wands.WandOfMagicMissile;
import pd.items.equipment.weapon.melee.special.TestWeapon;
import pd.items.equipment.weapon.missiles.ThrowingKnife;
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
		pd.items.consum.scrolls.Scroll.initLabels();
		pd.items.consum.potions.Potion.initColors();
		Ring.initGems();
		try {
			testChallengeGate();
			testCompleteLoadout();
			testLegacyContainersAndTome();
			testDummyMechanicsAndEdgeSafety();
			testBilingualResources();
			System.out.println("SPS测试模式开局通过：充满的多利亚石板、25条异界路线、8条挑战路线、完整物资数量、十二枚+10戒指、20000金币、20瓶许愿魔药、默认初始生命及玩偶机制均正常。");
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
		//SPSEXPD: 测试时间的重随机会不应污染普通模式
		check(hero.perkRerolls == Hero.DEFAULT_PERK_REROLLS, "普通模式重随次数被测试模式污染");
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
		//SPSEXPD: 测试时间给足重随机会，方便反复刷候选
		check(hero.perkRerolls == 999, "测试时间没有给足重随机会");

		Class<?>[] uniqueItems = {
				Elevator.class, SkillBook.class, ScrollHolder.class,
				PotionBandolier.class, ShoppingCart.class, MagicalHolster.class, Palantir.class,
				//SPSEXPD: 竹背篓（只装投掷果实与大型果实）
				BambooBasket.class,
				SoulCollect.class, PowerHand.class, TomeOfMastery.class, TestWeapon.class,
				EasterEgg.class, AflyEgg.class, GoldDragonEgg.class, SaveYourLife.class,
				FourClover.class, MasterThievesArmband.class
		};
		for (Class<?> type : uniqueItems) check(countExact(hero, type) == 1, "测试物资缺失或重复：" + type.getSimpleName());

		check(countExact(hero, ScrollOfIdentify.class) == 199, "鉴定卷轴数量不是199");
		check(countExact(hero, ScrollOfMagicMapping.class) == 199, "地图卷轴数量不是199");
		check(countExact(hero, MoonCake.class) == 199, "月饼数量不是199");
		check(countExact(hero, PotionOfMindVision.class) == 199, "灵视药剂数量不是199");
		Class<?>[] nornStones = {YellowNornStone.class, BlueNornStone.class, OrangeNornStone.class,
				PurpleNornStone.class, GreenNornStone.class};
		for (Class<?> type : nornStones) check(countExact(hero, type) == 199, type.getSimpleName() + "数量不是199");
		Class<?>[] tens = {Seedpod.Seed.class, Dewcatcher.Seed.class, ScrollOfDummy.class,
				PotionOfHealing.class, ScrollOfPsionicBlast.class, Hamburger.class,
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
		check(Dungeon.gold == 20000, "测试模式金币没有恢复到规定值（20000）");
		//SPSEXPD: 初始血量恢复正常——不再被 TEST_TIME 拉高
		check(hero.HTBoost == 0 && hero.HT == hero.baseLevelHT(),
				"测试模式不应改变初始最大生命（HTBoost=" + hero.HTBoost + "）");
		check(countExact(hero, WishPotion.class) == 20, "许愿魔药数量不是20");
		//SPSEXPD: 不再发放大小果实/蔬菜/二次产物
		for (Item item : hero.belongings) {
			String pkg = item.getClass().getPackageName();
			check(!pkg.startsWith("pd.items.consum.food.fruit")
							&& !pkg.startsWith("pd.items.consum.food.vegetable")
							&& !pkg.startsWith("pd.items.consum.food.processed"),
					"测试模式仍发放果实/蔬菜/二次产物：" + item.getClass().getSimpleName());
		}
		check(Dungeon.depth == 17 && Dungeon.branch == 0, "测试模式不应改写楼层深度（出生点留在 0 层由 Dungeon.init 决定），但应归零分支");
		check(hero.belongings.backpack.capacity() >= 64, "测试模式背包容量不足64格");

		//SPSEXPD: 包裹标签固定顺序——绒布包-卷轴筒-药水箱-购物车-竹背篓-魔法套筒-暗器袋-草靶子-钥匙串
		java.util.ArrayList<pd.items.equipment.bags.Bag> bags = hero.belongings.getBags();
		int lastOrder = -1;
		for (pd.items.equipment.bags.Bag b : bags) {
			if (b == hero.belongings.backpack) continue;
			check(b.bagOrder() >= lastOrder, "包裹标签顺序不是固定顺序（" + b.getClass().getSimpleName() + "）");
			lastOrder = b.bagOrder();
		}
		int cartIdx = -1, basketIdx = -1;
		for (int i = 0; i < bags.size(); i++) {
			if (bags.get(i) instanceof ShoppingCart) cartIdx = i;
			else if (bags.get(i) instanceof BambooBasket) basketIdx = i;
		}
		check(cartIdx >= 0 && basketIdx == cartIdx + 1, "竹背篓的标签页没有紧跟购物车");
	}

	private static void testLegacyContainersAndTome() {
		//SPS: 种子包已取消（绒布袋 VelvetPouch 为其替代品），相关断言移除

		MagicalHolster holster = new MagicalHolster();
		//SPS: 法器包与魔法套筒已合并（取大：容量 30、价值 60）
		check(holster.capacity() == 35 && holster.value() == 60, "魔法套筒容量或价值错误");
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
		String[] keys = {"items.equipment.bags.wandholster.name=",
				"items.tomeofmastery.name=", "items.consum.scrolls.scrollofdummy.name=",
				"items.consum.scrolls.scrollofdummy$minidummy.name="};
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
