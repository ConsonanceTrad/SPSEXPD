package pd.items.food.completefood;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.Poison;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Tar;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.mobs.YearBeast;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Heap;
import pd.items.Honeypot;
import pd.items.Item;
import pd.items.Recipe;
import pd.items.food.FishCracker;
import pd.items.food.Honey;
import pd.items.food.WaterItem;
import pd.items.food.fruit.Cloudberry;
import pd.items.food.fusion.Nut;
import pd.items.food.meatfood.HarmPoop;
import pd.items.food.meatfood.Meat;
import pd.items.food.meatfood.MeatFood;
import pd.items.food.staplefood.Pasty;
import pd.items.food.vegetable.Truffles;
import pd.items.food.vegetable.Vegetable;
import pd.items.journalpages.Vault;
import pd.items.medicine.Pill;
import pd.items.quest.AdventureJournal;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import pd.tiles.CustomTilemap;
import pd.windows.WndIronMaker;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

public final class SpsSpecialFoodTest {

	private static final String[] ICON_HASHES = {
			"4672312F2FD9D213374A3A9C3B37C888AC7A59F9A1166D3C1926F7E4BF503B83",
			"D244AFE1ACC2158D61D77A2D59ABBE4EE1B7AAC4A2889F6092CD3493066F8146",
			"7BF175B04C391F51825023826130003D4D083ED7CCD107B99360B8A24F60F9E6",
			"CA4C69A473BC589E9FA317A54E1EE8D1CC40E88C389A2F45B3D7BFD308DF8A92",
			"568D47D5BF38826AF2E8F645099442A710DEC5EAF4C1331C51CBC9402FBF1EF9",
			"04993C853CD69797993686AA73D5AB5FFDAABE06030898C7812F952AA7E2194B"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		pd.items.scrolls.Scroll.initLabels();
		pd.items.potions.Potion.initColors();
		pd.items.rings.Ring.initGems();
		pd.items.Generator.fullReset();
		Random.pushGenerator(0x5350535350454349L);
		try {
			testDefinitions();
			testEffects();
			testYearFoodRoute();
			testCloudberry();
			testStartingFoods();
			testAcquisitionRecipes();
			testNpcSources();
			testEmptyHeapReaction();
			testIcons();
			System.out.println("SPS特殊食物测试通过：10种食物、状态、永久生命、年糕召唤、年兽坐标掉落、公共开局物品和6个原始图标均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testCloudberry() {
		Dungeon.depth = 12;
		Dungeon.branch = 0;
		boolean plain = false;
		boolean floating = false;
		boolean regenerating = false;
		for (int i = 0; i < 200 && !(plain && floating && regenerating); i++) {
			Hero hero = hero(100);
			new TestCloudberry().apply(hero);
			check(hero.buff(HasteBuff.class) != null, "云莓没有始终给予旧版加速");
			Levitation levitation = hero.buff(Levitation.class);
			BerryRegeneration regen = hero.buff(BerryRegeneration.class);
			if (levitation == null && regen == null) plain = true;
			if (levitation != null && regen == null) floating = true;
			if (levitation != null && regen != null && regen.level() == 15) regenerating = true;
		}
		check(plain && floating && regenerating, "云莓的6/3/1旧版随机分支不完整");

		Dungeon.depth = 14;
		Dungeon.branch = AdventureJournal.branchFor(22);
		boolean chaosRegen = false;
		for (int i = 0; i < 200; i++) {
			Hero hero = hero(100);
			new TestCloudberry().apply(hero);
			check(hero.buff(Levitation.class) == null,
					"有效深度85的混沌路线仍从云莓获得漂浮");
			chaosRegen |= hero.buff(BerryRegeneration.class) != null;
		}
		check(chaosRegen, "混沌路线云莓的十分之一莓果恢复分支失效");
		Dungeon.branch = 0;
	}

	private static void testDefinitions() {
		check(new FishCracker().energy == 200f && new FishCracker().value() == 1000
				&& new FishCracker().image == ItemSpriteSheet.SPS_FISH_FOOD, "鱼饼定义错误");
		Honey honey = new Honey();
		honey.random();
		check(honey.energy == 50f && honey.quantity() == 1 && honey.value() == 500
				&& honey.image == ItemSpriteSheet.SPS_HONEY, "蜂蜜定义错误");
		check(new PetFood().energy == 10f && new PetFood().value() == 1, "宠物口粮定义错误");
		check(new FishPetFood().energy == 100f && new FishPetFood().value() == 1
				&& new FishPetFood().image == ItemSpriteSheet.SPS_FISH_PET_FOOD, "鱼味宠物口粮定义错误");
		check(new HarmPoop() instanceof MeatFood && new HarmPoop().energy == 10f
				&& new HarmPoop().value() == 2, "有害秽物类型或定义错误");
		check(new Mediummeat().energy == 180f && new Mediummeat().value() == 3, "七分熟肉排定义错误");
		check(new NutCake().energy == 450f && new NutCake().value() == 1, "坚果布丁定义错误");
		check(new Sishimi().energy == 180f && new Sishimi().value() == 3, "生鱼片定义错误");
		check(new YearFood().energy == 150f && new YearFood().value() == 400, "年糕定义错误");
		check(new ZongZi().energy == 600f && new ZongZi().value() == 60 && !new ZongZi().stackable,
				"粽子定义错误");
	}

	private static void testEffects() {
		Hero hero = hero(100);
		int oldHT = hero.HT;
		new TestHoney().apply(hero);
		check(hero.HT >= oldHT + 2 && hero.HT <= oldHT + 3 && hero.HP == hero.HT, "蜂蜜永久生命错误");

		hero = hero(100);
		new TestHarmPoop().apply(hero);
		check(hero.buff(Poison.class) != null && hero.buff(Slow.class) != null
				&& hero.buff(Slow.class).cooldown() >= 5f, "有害秽物中毒或迟缓错误");

		hero = hero(100);
		new Mediummeat().doEat(hero);
		check(hero.buff(AttackUp.class).level() == 60, "七分熟肉排攻击提升错误");

		hero = hero(100);
		hero.HP = 20;
		oldHT = hero.HT;
		new NutCake().doEat(hero);
		int increase = hero.HT - oldHT;
		check(increase >= 7 && increase <= 13, "坚果布丁永久生命错误");
		check(hero.HP == 20 + (oldHT + increase - 20) / 2 + increase, "坚果布丁治疗顺序错误");
		check(hero.buff(ShieldArmor.class).level() == oldHT / 3, "坚果布丁物理护盾错误");

		hero = hero(100);
		new Sishimi().doEat(hero);
		check(hero.buff(MagicArmor.class).level() == 20, "生鱼片魔法护盾错误");

		hero = hero(100);
		new ZongZi().doEat(hero);
		check(hero.buff(Tar.class) != null && hero.buff(Slow.class) != null
				&& hero.buff(Slow.class).cooldown() >= 30f
				&& hero.buff(MagicArmor.class).level() == 25
				&& hero.buff(AttackUp.class).level() == 20, "粽子状态效果错误");
	}

	private static void testYearFoodRoute() {
		Actor.clear();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Dungeon.depth = 25;
		Hero hero = hero(100);
		hero.pos = 8 * 16 + 8;
		int oldHT = hero.HT;
		new YearFood().doEat(hero);
		check(hero.HT >= oldHT + 6 && hero.HT <= oldHT + 10, "年糕在25层没有提供两次永久生命");
		check(level.mobs.size() == 1 && level.mobs.iterator().next() instanceof YearBeast,
				"年糕在25层没有召唤正确的年兽");
		YearBeast beast = (YearBeast)level.mobs.iterator().next();
		check(!level.adjacent(beast.pos, hero.pos), "年兽生成在英雄相邻格");

		beast.die(hero);
		Heap reward = level.heaps.get(beast.pos);
		check(reward != null && reward.peek() instanceof Vault, "年兽速杀没有掉落宝地坐标");
		AdventureJournal journal = new AdventureJournal();
		check(journal.addPage((Vault)reward.peek()) && journal.isUnlocked(6), "宝地坐标无法解锁春节庭院");

		Actor.clear();
		level = new TestLevel();
		Dungeon.level = level;
		Dungeon.depth = AdventureJournal.anchorDepth(22);
		Dungeon.branch = AdventureJournal.branchFor(22);
		hero = hero(100);
		hero.pos = 8 * 16 + 8;
		oldHT = hero.HT;
		new YearFood().doEat(hero);
		check(hero.HT >= oldHT + 3 && hero.HT <= oldHT + 5,
				"混沌85层的年糕错误获得了主线25层双倍生命");
		check(level.mobs.isEmpty(), "混沌85层的年糕错误召唤了主线25层年兽");
		Dungeon.branch = 0;
	}

	private static void testStartingFoods() {
		for (HeroClass heroClass : HeroClass.playableClasses()) {
			Hero hero = start(heroClass, 0);
			check(has(hero, Pasty.class) && has(hero, NutCake.class), heroClass + "缺少公共开局食物");
		}
		Hero festival = start(HeroClass.WARRIOR, 3);
		check(has(festival, YearFood.class), "皮肤3缺少年糕");
	}

	private static void testAcquisitionRecipes() throws Exception {
		checkRecipe(ZongZi.class, 1, new Pasty(), new Vegetable(), new Meat());
		checkRecipe(NutCake.class, 1, new Pasty(), new Nut(), new Honey());
		checkRecipe(PetFood.class, 1, new Nut(), new Nut(), new WaterItem());
		checkRecipe(Sishimi.class, 1, new WaterItem(), new Meat());
		checkRecipe(Honey.class, 2, new Honeypot());
		checkRecipe(Honey.class, 2, new Honeypot.ShatteredPot());
		checkRecipe(Honey.class, 1, new Truffles());

		Method recipe = WndIronMaker.class.getDeclaredMethod("recipe", ArrayList.class);
		recipe.setAccessible(true);
		ArrayList<Item> ingredients = new ArrayList<>(Arrays.asList(new WaterItem(), new Meat()));
		check(recipe.invoke(null, ingredients) instanceof Mediummeat,
				"铁砧没有恢复水加肉制作七分熟肉排的路线");
	}

	private static void testNpcSources() {
		check(new TownNpc().configure(TownNpc.Spec.HBB).SupercreateLoot() instanceof FishCracker,
				"HBB特殊掉落没有恢复鱼饼");
		check(new TownNpc().configure(TownNpc.Spec.SHOWER).SupercreateLoot() instanceof FishPetFood,
				"Shower特殊掉落没有恢复鱼味宠物口粮");
	}

	private static void checkRecipe(Class<? extends Item> output, int quantity, Item... input) {
		ArrayList<Item> ingredients = new ArrayList<>(Arrays.asList(input));
		ArrayList<Recipe> matches = Recipe.findRecipes(ingredients);
		Recipe selected = null;
		for (Recipe recipe : matches) {
			Item sample = recipe.sampleOutput(ingredients);
			if (sample != null && output.isInstance(sample) && sample.quantity() == quantity) {
				selected = recipe;
				break;
			}
		}
		check(selected != null, "炼金配方未注册：" + output.getSimpleName());
		check(selected.cost(ingredients) == 0, "旧版免费炼金配方产生了能量消耗：" + output.getSimpleName());
		Item result = selected.brew(ingredients);
		check(result != null && output.isInstance(result) && result.quantity() == quantity,
				"炼金配方产物错误：" + output.getSimpleName());
		for (Item ingredient : ingredients) {
			check(ingredient.quantity() == 0, "炼金配方没有正确消耗材料：" + output.getSimpleName());
		}
	}

	private static void testEmptyHeapReaction() {
		Dungeon.level = null;
		Heap heap = new Heap();
		heap.items.add(new Pill());
		heap.shockhit();
		check(heap.isEmpty(), "闪电没有移除地面药剂");
	}

	private static void testIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集尺寸错误");
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		for (int slot = 0; slot < ICON_HASHES.length; slot++) {
			ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
			int left = 144 + slot * 16;
			for (int y = 832; y < 848; y++) {
				for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(ICON_HASHES[slot].equals(toHex(digest.digest(pixels.array()))),
					"第" + (slot + 1) + "个特殊食物原始图标错误");
		}
	}

	private static Hero start(HeroClass heroClass, int skin) {
		Actor.clear();
		Dungeon.level = null;
		Dungeon.gold = 0;
		Dungeon.LimitedDrops.reset();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.skin = skin;
		Dungeon.hero = hero;
		heroClass.initHero(hero);
		return hero;
	}

	private static Hero hero(int health) {
		Hero hero = new Hero();
		hero.HTBoost = health - Hero.STARTING_HT;
		hero.updateHT(false);
		hero.HP = hero.HT;
		Dungeon.hero = hero;
		return hero;
	}

	private static boolean has(Hero hero, Class<? extends Item> type) {
		return hero.belongings.getItem(type) != null;
	}

	private static String toHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestHoney extends Honey {
		void apply(Hero hero) { satisfy(hero); }
	}

	private static final class TestCloudberry extends Cloudberry {
		void apply(Hero hero) { onEat(hero); }
	}

	private static final class TestHarmPoop extends HarmPoop {
		void apply(Hero hero) { doEat(hero); }
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
			customTiles = new ArrayList<CustomTilemap>();
			customTerrain = new ArrayList<CustomTilemap>();
			customWalls = new ArrayList<CustomTilemap>();
			buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
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

	private SpsSpecialFoodTest() { }
}
