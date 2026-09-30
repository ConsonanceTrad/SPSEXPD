package pd.items;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.blobs.Water;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Silent;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Tar;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Zombie;
import pd.actors.mobs.Mob;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Firebloom;
import pd.plants.Dewcatcher;
import pd.plants.Plant;
import pd.items.food.Honey;
import pd.items.potions.Potion;
import pd.items.potions.PotionOfMight;
import pd.items.scrolls.Scroll;
import pd.sprites.ItemSpriteSheet;
import pd.windows.WndIronMaker;
import watabou.noosa.Game;
import watabou.utils.Bundle;
import watabou.utils.FileUtils;
import watabou.utils.Random;
import watabou.utils.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

public final class SpsLegacyUtilityItemsTest {

	private static final String[] ICON_HASHES = {
			"ECF21EED1941F7DA7BFEA8B62932CA6D9BF867F80C2A49BA605A68533AA980BF",
			"28371021E5544BCF6055A99C9B6CAA4069C0FE25FD3769A9D191CBDCFBE6CEE3",
			"53A10F4C61B52CF810C3265153FB65CF1993524E379A8B2284590A850CE94FE4",
			"E37BA665C00E82C96DADB3DD9E79335BA29C583E0AF46043DF20CDEE8416F8AE",
			"C6628B9D166F06EA44362FE4D00B0E2517C9C9AF6263B87C39EC318604122973"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-utility-items" + File.separator);
		Game.version = "test";
		Badges.loadGlobal();
		try {
			testDewVialCompatibility();
			testDewAndForge();
			testDewUpgradeChain();
			testItemPhobiaChallenge();
			testListlessChallenge();
			testDewRejectionChallenge();
			testGoldItems();
			testMightAndAnkh();
			testSourcesAndTransmutation();
			testResourcesAndSprites();
			System.out.println("SPS通用道具通过：绿色露珠、金币袋、S币、根骨之瓶、十字架、来源、双语资源与原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testDewVialCompatibility() throws Exception {
		DewVial vial = new DewVial(35, 70);
		check(vial.checkVol() == 35 && vial.checkVolEx() == 70, "DewVial构造容量错误");
		vial.applySpsUpgrade(Waterskin.UpgradeMode.ACCURATE);
		Bundle bundle = new Bundle();
		vial.storeInBundle(bundle);
		DewVial restored = new DewVial();
		restored.restoreFromBundle(bundle);
		check(restored.checkVol() == 100 && restored.checkVolEx() == 105
				&& restored.upgradeMode() == Waterskin.UpgradeMode.ACCURATE,
				"DewVial没有复用完整露珠容量或强化模式存档");
		String heroClass = java.nio.file.Files.readString(Paths.get("../java/pd/actors/hero/HeroClass.java"), StandardCharsets.UTF_8);
		check(heroClass.contains("new DewVial()"), "普通职业开局没有实际使用旧版DewVial精确类名");
	}

	private static void testDewAndForge() throws Exception {
		GreenDewdrop dew = new GreenDewdrop();
		check(dew.image == ItemSpriteSheet.SPS_GREEN_DEWDROP, "绿色露珠图标常量错误");
		Random.pushGenerator(0x475245454E444557L);
		try {
			for (int i = 0; i < 100; i++) {
				int value = dew.dewValue();
				check(value >= 10 && value <= 29, "绿色露珠储存量越界：" + value);
			}
		} finally { Random.popGenerator(); }

		Method recipe = WndIronMaker.class.getDeclaredMethod("recipe", ArrayList.class);
		recipe.setAccessible(true);
		ArrayList<Item> seed = new ArrayList<>();
		seed.add(new Firebloom.Seed());
		check(recipe.invoke(null, seed) instanceof GreenDewdrop, "单颗种子没有锻造成绿色露珠");
	}

	private static void testDewUpgradeChain() throws Exception {
		freshLevel();
		Hero hero = freshHero();
		hero.HP = 0;
		RedDewdrop red = (RedDewdrop) new RedDewdrop().quantity(3);
		YellowDewdrop yellow = (YellowDewdrop) new YellowDewdrop().quantity(3);
		VioletDewdrop violet = (VioletDewdrop) new VioletDewdrop().quantity(3);
		check(red.healingValue(hero) == 30 && red.dewValue() == 45, "红露珠堆叠治疗或储存量错误");
		check(yellow.healingValue(hero) == 15 && yellow.dewValue() == 15, "黄露珠堆叠治疗或储存量错误");
		check(violet.healingValue(hero) == 100 && violet.dewValue() == 90, "紫露珠堆叠治疗或储存量错误");
		Dungeon.depth = pd.items.quest.AdventureJournal.anchorDepth(22);
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(22);
		hero.HT = 1000;
		hero.HP = 0;
		check(red.healingValue(hero) == 78 && yellow.healingValue(hero) == 63
				&& violet.healingValue(hero) == 198,
				"彩色露珠没有按混沌领域旧版85层深度治疗");
		Dungeon.depth = 1;
		Dungeon.branch = 0;

		hero.HT = 100;
		hero.HP = 60;
		hero.subClass = HeroSubClass.NONE;
		Waterskin ordinary = new Waterskin(100, 0);
		check(ordinary.consumeDrink(hero) && hero.HP == 100 && ordinary.checkVol() == 84,
				"普通职业饮用没有按每滴2.5%治疗或消耗");
		hero.HP = 60;
		hero.subClass = HeroSubClass.WARDEN;
		Waterskin warden = new Waterskin(100, 0);
		check(warden.consumeDrink(hero) && hero.HP == 100 && warden.checkVol() == 90,
				"守望者饮用没有按每滴4%治疗或消耗");

		Dungeon.wings = false;
		Waterskin firstUpgrade = new Waterskin(40, 10);
		firstUpgrade.applySpsUpgrade(Waterskin.UpgradeMode.RANDOM_BLESS);
		check(firstUpgrade.checkVol() == 100 && firstUpgrade.checkVolEx() == 50
				&& firstUpgrade.upgradeMode() == Waterskin.UpgradeMode.RANDOM_BLESS,
				"露珠瓶第一次升级没有填充容量、保存溢出或模式");
		Dungeon.wings = true;
		Waterskin wingCapacity = new Waterskin();
		wingCapacity.fill();
		check(wingCapacity.checkVol() == 200, "飞翼升级没有把普通容量提升到200");

		Dungeon.dewWater = false;
		Dungeon.wings = false;
		Vialupdater updater = new Vialupdater();
		check(updater.collect(hero.belongings.backpack), "露珠强化器无法放入背包");
		updater.execute(hero, Vialupdater.AC_USE);
		check(Dungeon.dewWater && Dungeon.wings
				&& hero.belongings.getItem(Vialupdater.class) == null,
				"露珠强化器没有消耗自身并开启露珠水与飞翼升级");

		TestLevel level = freshLevel();
		hero = freshHero();
		int flowerPot = hero.pos + 1;
		level.map[flowerPot] = Terrain.FLOWER_POT;
		new Waterskin().waterArea(hero);
		Plant flower = level.plants.get(flowerPot);
		Field seedClass = Plant.class.getDeclaredField("seedClass");
		seedClass.setAccessible(true);
		check(flower != null && Arrays.asList(Generator.Category.SEED4.classes).contains(seedClass.get(flower)),
				"露珠种植没有在花盆生成SEED4植物");

		int oldGrass = hero.pos + 2;
		level.map[oldGrass] = Terrain.OLD_HIGH_GRASS;
		level.buildFlagMaps();
		Water water = new Water();
		water.seed(level, oldGrass, 40);
		Method evolve = Water.class.getDeclaredMethod("evolve");
		evolve.setAccessible(true);
		evolve.invoke(water);
		check(level.map[oldGrass] == Terrain.HIGH_GRASS || level.map[oldGrass] == Terrain.GRASS,
				"露珠水没有处理旧式高草");

		Buff.affect(hero, Tar.class);
		Buff.affect(hero, STRDown.class, 10f);
		check(hero.buff(Tar.class) != null && hero.buff(STRDown.class) != null, "清洗测试负面状态没有附着");
		Waterskin.cleanse(hero);
		check(hero.buff(Tar.class) == null && hero.buff(STRDown.class) == null,
				"露珠清洗没有移除焦油与力量衰减");

		String tinkerer = java.nio.file.Files.readString(Paths.get("../java/pd/windows/WndTinkerer.java"), StandardCharsets.UTF_8);
		String tinkerer2 = java.nio.file.Files.readString(Paths.get("../java/pd/windows/WndTinkerer2.java"), StandardCharsets.UTF_8);
		String triangle = java.nio.file.Files.readString(Paths.get("../java/pd/levels/TrianglePLevel.java"), StandardCharsets.UTF_8);
		check(tinkerer.contains("applySpsUpgrade") && tinkerer.contains("Dungeon.dewDraw")
				&& tinkerer.contains("Dungeon.dewWater"), "第1层工匠没有提供两种第一次升级");
		check(tinkerer2.contains("Dungeon.dewNorn = true"), "第12层工匠没有开启露珠瓶二阶能力");
		check(triangle.contains("new Vialupdater()"), "三角维度没有生成露珠强化器");
	}

	private static void testDewRejectionChallenge() throws Exception {
		check(Challenges.DEW_REJECTION == 1024
				&& Challenges.DEW_REJECTION != Challenges.SWARM_INTELLIGENCE,
				"露珠排斥挑战与破碎集群智能发生位冲突");
		check(Challenges.MAX_VALUE == 262143 && Challenges.MAX_CHALS == 18
				&& Challenges.MASKS[13] == Challenges.DEW_REJECTION
				&& Challenges.MASKS[14] == Challenges.SPS_DARKNESS
				&& Challenges.MASKS[15] == Challenges.ABRASION
				&& Challenges.MASKS[16] == Challenges.ELE_STOME
				&& Challenges.MASKS[17] == Challenges.TEST_TIME,
				"露珠排斥挑战没有独立接入挑战选择范围");

		freshLevel();
		check(Waterskin.dewCost(25) == 25 && Mob.spsDewBurstOffsets().length == 9,
				"未开启露珠排斥时错误改变了露珠消耗或爆发范围");
		Dungeon.challenges = Challenges.DEW_REJECTION;
		check(Waterskin.dewCost(25) == 35 && Waterskin.dewCost(100) == 110,
				"露珠排斥没有为每项露珠能力增加10点消耗");
		check(Mob.spsDewBurstOffsets().length == 4, "露珠排斥没有把露珠爆发缩小到四方向");

		Dungeon.dewWater = true;
		Waterskin shortRefine = new Waterskin(100, 0);
		check(!shortRefine.actions(freshHero()).contains("REFINE"),
				"露珠排斥下100点露珠错误开放110点提纯");
		shortRefine.setVol(100, 10);
		check(shortRefine.actions(Dungeon.hero).contains("REFINE"),
				"露珠排斥下110点露珠没有开放提纯");

		Actor.clear();
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		Dungeon.hero = hero;
		Method challengeStarts = HeroClass.class.getDeclaredMethod("applySpsChallengeStarts", Hero.class);
		challengeStarts.setAccessible(true);
		challengeStarts.invoke(null, hero);
		int seeds = 0;
		for (Item item : hero.belongings) {
			if (item instanceof Dewcatcher.Seed) seeds += item.quantity();
		}
		check(seeds == 2, "露珠排斥开局没有补偿两颗集露草种子：" + seeds);
	}

	private static void testItemPhobiaChallenge() throws Exception {
		check(Challenges.ITEM_PHOBIA == 2048
				&& Challenges.MASKS[9] == Challenges.ITEM_PHOBIA,
				"恐物幻觉挑战没有使用独立挑战位");

		freshLevel();
		TestPotion potion = new TestPotion();
		check(potion.drinkTime() == 1f, "未开启恐物幻觉时错误延长了饮药时间");
		Hero hero = freshHero();
		new TestScroll().applyPenalty(hero);
		check(hero.HP == 100 && hero.buff(Silent.class) == null,
				"未开启恐物幻觉时错误施加了卷轴惩罚");

		Dungeon.challenges = Challenges.ITEM_PHOBIA;
		check(potion.drinkTime() == 5f, "恐物幻觉没有把饮药时间延长到5回合");
		hero = freshHero();
		new TestScroll().applyPenalty(hero);
		check(hero.HP == 90 && hero.buff(Silent.class) != null,
				"恐物幻觉没有造成最大生命10%伤害并沉默英雄");

		Dungeon.gold = 25;
		Method challengeStarts = HeroClass.class.getDeclaredMethod("applySpsChallengeStarts", Hero.class);
		challengeStarts.setAccessible(true);
		challengeStarts.invoke(null, hero);
		check(Dungeon.gold == 1025, "恐物幻觉开局没有补偿1000金币：" + Dungeon.gold);
	}

	private static void testListlessChallenge() throws Exception {
		check(Challenges.LISTLESS == 4096 && Challenges.MASKS[10] == Challenges.LISTLESS,
				"精神萎靡挑战没有使用独立挑战位");

		freshLevel();
		Hero normal = new Hero();
		normal.heroClass = HeroClass.WARRIOR;
		Dungeon.hero = normal;
		Actor.add(normal);
		normal.earnExp(normal.maxExp(), SpsLegacyUtilityItemsTest.class);
		check(normal.lvl == 2 && normal.permanentHT() == 35 && normal.HP == 35,
				"普通升级生命成长被精神萎靡逻辑污染");

		freshLevel();
		Dungeon.challenges = Challenges.LISTLESS;
		Hero hero = new Hero();
		hero.heroClass = HeroClass.WARRIOR;
		Dungeon.hero = hero;
		Actor.add(hero);
		hero.earnExp(hero.maxExp(), SpsLegacyUtilityItemsTest.class);
		check(hero.lvl == 2 && hero.permanentHT() == 32 && hero.HT == 32 && hero.HP == 31,
				"精神萎靡没有按升级上限+2、当前生命+1成长");

		Method challengeStarts = HeroClass.class.getDeclaredMethod("applySpsChallengeStarts", Hero.class);
		challengeStarts.setAccessible(true);
		challengeStarts.invoke(null, hero);
		check(hero.belongings.getItem(PotionOfMight.class) != null
				&& hero.belongings.getItem(Honey.class) != null,
				"精神萎靡开局缺少根骨药水或蜂皇浆");
	}

	private static void testGoldItems() {
		TestLevel level = freshLevel();
		Hero hero = freshHero();
		GoldBag bag = new GoldBag();
		bag.quantity(2).collect(hero.belongings.backpack);
		Dungeon.gold = 0;
		check(bag.use(hero) && Dungeon.gold == 10000 && bag.quantity() == 1,
				"金币袋没有逐袋兑现10000金币");
		check(bag.value() == 10000, "金币袋价值错误");

		Gold gold = new Gold();
		Dungeon.gold = 10000;
		check(!gold.actions(hero).contains(Gold.AC_MAKEBAG), "10000金币时错误开放装袋");
		Dungeon.gold = 10001;
		check(gold.actions(hero).contains(Gold.AC_MAKEBAG) && gold.makeBag(hero), "金币大于10000时无法装袋");
		check(Dungeon.gold == 1 && level.heaps.get(hero.pos).peek() instanceof GoldBag,
				"金币装袋没有扣除10000或生成金币袋");

		SpecialCoin coin = new SpecialCoin(100);
		check(coin.actions(hero).isEmpty() && coin.quantity() == 100 && coin.value() == 0,
				"S币不应进入普通金币系统或提供物品动作");
	}

	private static void testMightAndAnkh() {
		freshLevel();
		Hero hero = freshHero();
		hero.STR = 8;
		hero.HP = 1;
		MitBottle might = new MitBottle();
		might.quantity(2).collect(hero.belongings.backpack);
		int oldPermanentHT = hero.permanentHT();
		check(might.use(hero), "根骨之瓶无法使用");
		check(hero.STR == 9, "根骨之瓶力量错误：" + hero.STR);
		check(hero.permanentHT() == oldPermanentHT + 10,
				"根骨之瓶永久生命错误：" + oldPermanentHT + " -> " + hero.permanentHT());
		check(hero.HP == hero.HT, "根骨之瓶没有回满生命：" + hero.HP + "/" + hero.HT);
		check(might.quantity() == 1, "根骨之瓶没有逐瓶消耗：" + might.quantity());

		Waterskin waterskin = new Waterskin(0, 100);
		waterskin.collect(hero.belongings.backpack);
		UnBlessAnkh cross = new UnBlessAnkh();
		cross.collect(hero.belongings.backpack);
		check(cross.actions(hero).contains(UnBlessAnkh.AC_BLESS), "100点额外露水没有开放十字架祝福");
		check(cross.bless(hero), "十字架无法祝福");
		check(hero.belongings.getItem(UnBlessAnkh.class) == null
				&& hero.belongings.getItem(Ankh.class) != null && waterskin.checkVolEx() == 0,
				"十字架祝福没有转换为十字章或消耗100点额外露水");
	}

	private static void testSourcesAndTransmutation() {
		check(TransmutationBall.changeItem(new StrBottle()) instanceof MitBottle,
				"转换球没有把力量之瓶转为根骨之瓶");
		check(new Zombie().SupercreateLoot() instanceof UnBlessAnkh,
				"僵尸特殊掉落没有恢复十字架");
	}

	private static void testResourcesAndSprites() throws Exception {
		String zh = java.nio.file.Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String en = java.nio.file.Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		String[] keys = {"items.greendewdrop.name=", "items.gold.ac_makebag=", "items.goldbag.name=",
				"items.specialcoin.name=", "items.mitbottle.name=", "items.unblessankh.name=",
				"items.vialupdater.name=", "items.vialupdater.ac_use=", "items.vialupdater.desc="};
		for (String key : keys) check(zh.contains(key) && en.contains(key), "中英文资源缺少键：" + key);
		check(zh.contains("绿色露珠") && zh.contains("根骨之瓶") && !zh.contains("�"), "通用道具中文乱码或缺失");
		String miscZh = java.nio.file.Files.readString(Paths.get("messages/misc/zh/misc.properties"), StandardCharsets.UTF_8);
		String miscEn = java.nio.file.Files.readString(Paths.get("messages/misc/en/misc.properties"), StandardCharsets.UTF_8);
		check(miscZh.contains("challenges.dew_rejection=排异露珠")
				&& miscEn.contains("challenges.dew_rejection=dew rejection")
				&& miscZh.contains("challenges.item_phobia=恐物幻觉")
				&& miscEn.contains("challenges.item_phobia=item phobia")
				&& miscZh.contains("challenges.listless=精神萎靡")
				&& miscEn.contains("challenges.listless=listless")
				&& !miscZh.contains("�"), "旧版主挑战中英文资源缺失或乱码");

		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		int[] icons = {ItemSpriteSheet.SPS_GREEN_DEWDROP, ItemSpriteSheet.SPS_GOLD_BAG,
				ItemSpriteSheet.SPS_SPECIAL_COIN, ItemSpriteSheet.SPS_MIT_BOTTLE,
				ItemSpriteSheet.SPS_UNBLESS_ANKH};
		for (int i = 0; i < icons.length; i++) {
			check(ICON_HASHES[i].equals(hash(sheet, icons[i])), "第" + (i + 1) + "个通用道具不是旧版原始图标");
		}
	}

	private static TestLevel freshLevel() {
		Actor.clear();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Dungeon.challenges = 0;
		Dungeon.dewDraw = false;
		Dungeon.dewWater = false;
		Dungeon.dewNorn = false;
		Dungeon.wings = false;
		Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		return level;
	}

	private static Hero freshHero() {
		Hero hero = new Hero();
		hero.heroClass = HeroClass.WARRIOR;
		hero.pos = 8 + 8 * 16;
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static String hash(BufferedImage sheet, int itemIndex) throws Exception {
		int left = itemIndex % 16 * 16;
		int top = itemIndex / 16 * 16;
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>();
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

	private static final class TestPotion extends Potion {
		float drinkTime() {
			return timeToDrink();
		}
	}

	private static final class TestScroll extends Scroll {
		@Override public void doRead() { }
		void applyPenalty(Hero hero) {
			applySpsChallengePenalty(hero);
		}
	}

	private SpsLegacyUtilityItemsTest() { }
}
