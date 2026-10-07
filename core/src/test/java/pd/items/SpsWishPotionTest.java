package pd.items;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Poison;
import pd.actors.hero.Hero;
import pd.actors.hero.perks.Perk;
import pd.actors.mobs.Mob;
import pd.items.consum.food.processed.AetherLiquid;
import pd.items.consum.food.processed.CrystalShard;
import pd.items.consum.food.processed.HighEnergySpore;
import pd.items.consum.food.processed.WishPetal;
import pd.items.consum.potions.PotionOfConfusion;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.potions.PotionOfToxicGas;
import pd.items.consum.potions.elixirs.WishPotion;
import pd.items.consum.potions.wish.WishCatalog;
import pd.items.consum.potions.wish.WishEngine;
import pd.items.consum.potions.wish.WishFragment;
import pd.items.consum.potions.wish.WishMatcher;
import pd.items.consum.potions.wish.WishOnlyItem;
import pd.items.consum.potions.wish.WishRewardTable;
import pd.items.consum.potions.wish.WishSummon;
import pd.items.consum.potions.wish.WishTraitGrant;
import pd.items.consum.potions.wish.WishType;
import pd.items.misc.LuckyBadge;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import render.utils.data.SparseArray;
import render.utils.serialize.Reflection;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * SPSEXPD: 许愿魔药的无图形验证——配方、候选池与黑名单、名称词匹配、
 * 幸运等级门槛、彩蛋物品、效果愿望与死亡愿望，以及炼金指南接线。
 */
public final class SpsWishPotionTest {

	public static void main(String[] args) throws Exception {
		render.noosa.Game.version = "test";
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		//按简体中文加载资源，让候选池拿到真实中文名
		pd.messages.Messages.setup(pd.messages.Languages.CHI_SMPL);

		testRecipe();
		testCatalog();
		testMatcher();
		testTiers();
		testWishItems();
		testWishEffects();
		testKindWishes();
		testMobWishes();
		testTraitWishes();
		testDebugger();
		testGuideWiring();
		//SPSXPD: 占位符等未命名物品的 name() 为 null，标题整理（物品详情窗用）必须不崩
		check(pd.messages.Messages.titleCase(null).isEmpty(), "null 物品名不应让标题整理崩溃");

		System.out.println("SPS许愿魔药测试通过：配方、候选池与黑名单、名称词匹配、类型词（武器/神器/秘药等）、怪物召唤、特质精确名、幸运等级门槛、彩蛋物品、效果与死亡愿望、调试器与炼金指南接线均正常。");
		//SPSXPD: HeadlessApplication 的循环线程不是守护线程，测试跑完必须显式退出，
		//否则 JavaExec 任务会一直挂着（表现为 BUILD FAILED / WaitDelay expired）
		app.exit();
		System.out.flush();
		System.exit(0);
	}

	//---- 配方 ----

	private static void testRecipe() {
		ArrayList<Item> inputs = ingredients(PotionOfConfusion.class, WishPetal.class,
				CrystalShard.class, AetherLiquid.class, HighEnergySpore.class);
		for (Item item : inputs) item.quantity(2);

		ArrayList<Recipe> recipes = Recipe.findRecipes(inputs);
		check(recipes.size() == 1 && recipes.get(0) == SpsAlchemyRecipes.WISH_POTION,
				"许愿魔药配方不是唯一匹配");
		check(recipes.get(0).cost(inputs) == 0, "许愿魔药不应消耗炼金能量");
		check(recipes.get(0).sampleOutput(inputs) instanceof WishPotion, "许愿魔药预览产物错误");

		Item result = recipes.get(0).brew(inputs);
		check(result instanceof WishPotion && result.quantity() == 1, "许愿魔药实际产物错误");
		for (Item item : inputs) check(item.quantity() == 1, "许愿魔药材料没有逐槽消耗一份");
	}

	//---- 候选池与黑名单 ----

	private static void testCatalog() {
		List<WishCatalog.Entry> entries = WishCatalog.entries();
		check(!entries.isEmpty(), "许愿候选池为空");

		boolean sawFragment = false;
		for (WishCatalog.Entry entry : entries) {
			String simple = entry.type.getSimpleName();
			check(!entry.name.isEmpty(), "候选池条目缺少匹配名：" + simple);
			check(entry.tier >= 1 && entry.tier <= WishRewardTable.MAX_TIER,
					"候选池奖励等级越界：" + simple);
			check(!WishCatalog.excluded(entry.type), "被排除的物品仍留在候选池：" + simple);
			check(!simple.startsWith("Test"), "调试物品进入候选池：" + simple);
			String pkg = entry.type.getPackageName();
			check(!pkg.startsWith("pd.items.quest")
							&& !pkg.startsWith("pd.items.specific")
							&& !pkg.startsWith("pd.items.misc"),
					"任务/剧情/角色专属物品进入候选池：" + simple);
			if (entry.type.equals(WishFragment.class)) {
				sawFragment = true;
				check(entry.wishOnly && entry.tier == WishOnlyItem.TIER, "彩蛋物品等级或许愿专属标记错误");
			}
		}
		check(sawFragment, "彩蛋物品没有进入候选池");
		check(WishCatalog.excluded(Gold.class), "金币没有被排除在许愿之外");
	}

	//---- 名称词匹配 ----

	private static void testMatcher() {
		List<WishCatalog.Entry> entries = WishCatalog.entries();

		WishCatalog.Entry hit = WishMatcher.best("potionofhealing", entries);
		check(hit != null && hit.type.equals(PotionOfHealing.class), "词匹配没有命中治疗药剂");
		check(WishMatcher.accuracy("potionofhealing", hit) >= WishMatcher.MIN_ACCURACY,
				"命中物品的准确度不足");

		WishCatalog.Entry chinese = WishMatcher.best("治疗药剂", entries);
		check(chinese != null && chinese.type.equals(PotionOfHealing.class),
				"中文名称没有命中治疗药剂：" + (chinese == null ? "null" : chinese.type.getSimpleName()));

		WishCatalog.Entry miss = WishMatcher.best("zzzz", entries);
		check(miss == null || WishMatcher.accuracy("zzzz", miss) < WishMatcher.MIN_ACCURACY,
				"无意义文本错误命中物品");

		List<String> tokens = WishMatcher.tokenize("治疗药剂");
		check(tokens.contains("药剂") && tokens.contains("治疗"), "中文 2-gram 分词缺失");
		List<String> singles = WishMatcher.tokenize("治愈");
		check(singles.contains("愈"), "中文单字分词缺失");

		//单个中文单字不足以命中（避免误匹配长描述与同字物品）
		check(WishMatcher.accuracy("毒", new WishCatalog.Entry(PotionOfToxicGas.class, 1, "剧毒气体药剂", "", false))
						< WishMatcher.MIN_ACCURACY,
				"单个中文单字不应命中物品名称");
	}

	//---- 幸运等级门槛 ----

	private static void testTiers() {
		check(WishRewardTable.maxTierForLuck(0) == 1, "幸运 0 应只能拿 1 级奖励");
		check(WishRewardTable.maxTierForLuck(4) == 1, "幸运 4 应只能拿 1 级奖励");
		check(WishRewardTable.maxTierForLuck(5) == 2, "幸运 5 应能拿 2 级奖励");
		check(WishRewardTable.maxTierForLuck(10) == 3, "幸运 10 应能拿 3 级奖励");
		check(WishRewardTable.maxTierForLuck(15) == 4, "幸运 15 应能拿 4 级奖励");
		check(WishRewardTable.grantTier(4, 0) == 1, "幸运不足时 4 级奖励没有降级");
		check(WishRewardTable.grantTier(3, 10) == 3, "幸运足够时错误降级");
		check(WishRewardTable.grantTier(1, 15) == 1, "低等级奖励被错误抬高");

		for (int tier = 1; tier < WishRewardTable.MAX_TIER; tier++) {
			check(WishRewardTable.traitChance(tier, 0) <= WishRewardTable.traitChance(tier + 1, 0),
					"许愿特质概率未随奖励等级单调不降");
			check(WishRewardTable.traitChance(tier, 5) >= WishRewardTable.traitChance(tier, 0),
					"许愿特质概率未随幸运单调不降");
		}
	}

	//---- 物品愿望：彩蛋物品与幸运门槛 ----

	private static void testWishItems() {
		Hero lucky = newHero();
		LuckyBadge badge = new LuckyBadge();
		badge.upgrade(15);
		badge.collect(lucky.belongings.backpack);
		check(LuckyBadge.luckBonus(lucky) >= 15, "测试幸运值构建失败：" + LuckyBadge.luckBonus(lucky));

		WishEngine.Result rich = WishEngine.wish(lucky, "wishfragment");
		check(rich.kind == WishEngine.Kind.ITEM, "许愿彩蛋物品没有被判为物品愿望：" + rich.kind);
		check(rich.item instanceof WishFragment, "许愿彩蛋物品没有命中许愿残片");
		check(rich.wishOnly, "彩蛋物品没有标记为只有许愿可得");
		check(rich.tier == WishOnlyItem.TIER, "高幸运没有给到彩蛋物品的最高等级");

		Hero poor = newHero();
		check(LuckyBadge.luckBonus(poor) == 0, "新英雄幸运值应为 0");
		WishEngine.Result weakened = WishEngine.wish(poor, "wishfragment");
		check(weakened.weakened, "幸运不足时彩蛋愿望没有被削弱");
		check(weakened.tier == 1, "幸运不足时应只发 1 级奖励");
		check(!(weakened.item instanceof WishFragment), "幸运不足时仍然拿到了彩蛋物品");
	}

	//---- 效果愿望与死亡愿望 ----

	private static void testWishEffects() {
		Hero hero = newHero();
		WishEngine.Result death = WishEngine.wish(hero, "死亡");
		check(death.kind == WishEngine.Kind.DEATH, "死亡愿望没有被判定为死亡");
		check(hero.HP <= 0, "死亡愿望没有让英雄死亡");

		hero = newHero();
		WishEngine.Result poisoned = WishEngine.wish(hero, "让我中毒");
		check(poisoned.kind == WishEngine.Kind.NEGATIVE, "中毒愿望没有被判定为负面效果：" + poisoned.kind);
		check(hero.buff(Poison.class) != null, "中毒愿望没有施加中毒");

		hero = newHero();
		WishEngine.Result buffed = WishEngine.wish(hero, "无敌");
		check(buffed.kind == WishEngine.Kind.BUFF, "无敌愿望没有被判定为增益效果：" + buffed.kind);

		hero = newHero();
		WishEngine.Result nothing = WishEngine.wish(hero, "   ");
		check(nothing.kind == WishEngine.Kind.NOTHING, "空愿望没有被判定为无事发生");
	}

	//---- 类型词愿望 ----

	private static void testKindWishes() {
		Hero hero = newHero();
		//给足幸运，让 tier4 的类型池（神器/秘药）也能正常命中，而不是被削弱成低级替代品
		LuckyBadge badge = new LuckyBadge();
		badge.upgrade(15);
		badge.collect(hero.belongings.backpack);
		Dungeon.depth = 5;
		Dungeon.branch = 0;

		//各类型池都非空（含本次并入候选池的魔药/秘药/法术/炸弹）
		String[] kinds = {WishType.WEAPON, WishType.MISSILE, WishType.ARMOR, WishType.WAND,
				WishType.RING, WishType.ARTIFACT, WishType.POTION, WishType.SCROLL,
				WishType.STONE, WishType.SEED, WishType.FOOD, WishType.MEDICINE,
				WishType.BREW, WishType.ELIXIR, WishType.SPELL, WishType.BOMB};
		for (String kind : kinds) {
			check(!WishCatalog.ofKind(kind).isEmpty(), "类型池为空：" + kind);
		}

		//类型词 → 从该类型池里抽取
		WishEngine.Result weapon = WishEngine.wish(hero, "武器");
		check(weapon.kind == WishEngine.Kind.ITEM && weapon.item != null, "「武器」没有给出物品");
		check(WishType.WEAPON.equals(kindOf(weapon.item.getClass())),
				"「武器」给出的不是武器：" + weapon.item.getClass().getSimpleName());

		WishEngine.Result elixir = WishEngine.wish(hero, "秘药");
		check(elixir.kind == WishEngine.Kind.ITEM && elixir.item != null, "「秘药」没有给出物品");
		check(WishType.ELIXIR.equals(kindOf(elixir.item.getClass())),
				"「秘药」给出的不是秘药：" + elixir.item.getClass().getSimpleName());

		WishEngine.Result artifact = WishEngine.wish(hero, "神器");
		check(artifact.kind == WishEngine.Kind.ITEM && artifact.item != null, "「神器」没有给出物品");
		check(WishType.ARTIFACT.equals(kindOf(artifact.item.getClass())),
				"「神器」给出的不是神器：" + artifact.item.getClass().getSimpleName());

		//具体物品名优先于类型词
		WishEngine.Result specific = WishEngine.wish(hero, "治疗药剂");
		check(specific.item instanceof PotionOfHealing, "「治疗药剂」被当成了泛指药剂");
	}

	/** 候选池里某个类的类型标签。 */
	private static String kindOf(Class<?> type) {
		for (WishCatalog.Entry entry : WishCatalog.entries()) {
			if (entry.type.equals(type)) return entry.kind;
		}
		return null;
	}

	//---- 怪物愿望 ----

	private static void testMobWishes() {
		Hero hero = newHero();
		Dungeon.depth = 5;
		Dungeon.branch = 0;
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Actor.clear();
		hero.pos = level.length() / 2;
		Dungeon.hero = hero;
		Actor.add(hero);

		ArrayList<Class<? extends Mob>> rotation = WishSummon.rotation();
		check(!rotation.isEmpty(), "5 层没有普通怪轮转表");
		for (Class<? extends Mob> type : rotation) {
			check(!pd.actors.mobs.npcs.NPC.class.isAssignableFrom(type),
					"怪物池混入了 NPC：" + type.getSimpleName());
		}

		//泛指"怪物"
		int before = level.mobs().size();
		WishEngine.Result generic = WishEngine.wish(hero, "怪物");
		check(generic.kind == WishEngine.Kind.MOB, "「怪物」没有召唤怪物：" + generic.kind);
		check(generic.summoned != null && rotation.contains(generic.summoned), "召唤的不是本层普通怪");
		check(level.mobs().size() == before + 1, "没有真的生成怪物");

		//点名一只本层普通怪
		Class<? extends Mob> named = rotation.get(0);
		String name = WishSummon.nameOf(named);
		if (name != null && !name.isEmpty()) {
			int beforeNamed = level.mobs().size();
			WishEngine.Result exact = WishEngine.wish(hero, name);
			check(exact.kind == WishEngine.Kind.MOB, "具名怪物愿望没有召唤：" + name);
			check(named.equals(exact.summoned), "具名怪物愿望命中了别的怪：" + name);
			check(level.mobs().size() == beforeNamed + 1, "具名怪物没有生成");
		}
	}

	//---- 特质愿望：名称必须分毫不差 ----

	private static void testTraitWishes() {
		Hero hero = newHero();
		Dungeon.depth = 5;
		Dungeon.branch = 0;

		Perk target = null;
		String targetName = null;
		for (Class<? extends Perk> type : Perk.Companion.allClasses()) {
			Perk perk = Reflection.newInstance(type);
			if (perk == null || !perk.isAcquireAllowed(hero)) continue;
			String title;
			try {
				title = perk.title();
			} catch (Throwable ignored) {
				continue;
			}
			if (title == null || title.isEmpty()) continue;
			target = perk;
			targetName = title;
			break;
		}
		check(target != null && targetName != null, "找不到可用于测试的可获得特质");
		Class<? extends Perk> targetType = target.getClass();

		//分毫不差 → 授予
		WishEngine.Result granted = WishEngine.wish(hero, targetName);
		check(granted.kind == WishEngine.Kind.TRAIT, "精确特质名没有获得特质：" + targetName);
		check(hero.heroPerk.has(targetType), "特质没有真的加到英雄身上：" + targetName);

		//多一个字符 → 不授予
		Hero other = newHero();
		WishEngine.Result miss = WishEngine.wish(other, targetName + "x");
		check(!other.heroPerk.has(targetType), "名字不完全一致也授予了特质");
		check(miss.kind != WishEngine.Kind.TRAIT, "名字不完全一致也被判为特质愿望：" + miss.kind);

		//已拥有时的再次许愿仍被识别为特质愿望
		WishEngine.Result again = WishEngine.wish(hero, targetName);
		check(again.kind == WishEngine.Kind.TRAIT, "已拥有时的特质愿望没有被识别");
	}

	//---- 调试器接入 ----

	@SuppressWarnings("unchecked")
	private static void testDebugger() throws Exception {
		java.lang.reflect.Method groups = pd.windows.WndDebugItems.class.getDeclaredMethod("groups");
		groups.setAccessible(true);
		java.util.LinkedHashMap<String, ArrayList<Class<? extends Item>>> map =
				(java.util.LinkedHashMap<String, ArrayList<Class<? extends Item>>>) groups.invoke(null);

		String found = null;
		for (java.util.Map.Entry<String, ArrayList<Class<? extends Item>>> entry : map.entrySet()) {
			if (entry.getValue().contains(WishPotion.class)) {
				found = entry.getKey();
				break;
			}
		}
		check(found != null, "调试器物品清单里没有许愿魔药");
		check("消耗品".equals(found), "许愿魔药没有归入调试器「消耗品」组，实际：" + found);
	}

	//---- 炼金指南接线 ----

	private static void testGuideWiring() throws Exception {
		String quickRecipe = readSource("../java/pd/ui/QuickRecipe.java");
		check(quickRecipe.contains("SpsAlchemyRecipes.WISH_POTION"), "炼金指南没有展示许愿魔药配方");
		check(quickRecipe.contains("new WishPotion()"), "炼金指南缺少许愿魔药产物");
		check(quickRecipe.contains("PotionOfConfusion"), "炼金指南许愿魔药缺少混乱药剂");

		String document = readSource("../java/pd/journal/Document.java");
		check(document.contains("许愿魔药"), "炼金指南正文没有提到许愿魔药");

		String zh = readSource("messages/items/zh/items.properties");
		String en = readSource("messages/items/en/items.properties");
		String[] keys = {
				"items.consum.potions.elixirs.wishpotion.name=",
				"items.consum.potions.elixirs.wishpotion.result_weakened=",
				"items.consum.potions.wish.wishfragment.name=",
				"items.consum.potions.wish.wishcoin.name=",
				"items.consum.potions.wish.wishstar.name="
		};
		for (String key : keys) {
			check(zh.contains(key), "中文资源缺少键：" + key);
			check(en.contains(key), "英文资源缺少键：" + key);
		}

		String zhJournal = readSource("messages/journal/zh/journal.properties");
		String enJournal = readSource("messages/journal/en/journal.properties");
		check(zhJournal.contains("许愿魔药"), "中文炼金指南正文没有提到许愿魔药");
		check(enJournal.contains("Wish Potion"), "英文炼金指南正文没有提到 Wish Potion");
	}

	//---- helpers ----

	private static Hero newHero() {
		Hero hero = new Hero();
		hero.HTBoost = 80;
		hero.updateHT(false);
		hero.HP = hero.HT;
		Dungeon.hero = hero;
		return hero;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Item> ingredients(Class<?>... types) {
		ArrayList<Item> result = new ArrayList<>();
		for (Class<?> type : types) result.add(Reflection.newInstance((Class<? extends Item>) type));
		return result;
	}

	private static String readSource(String relative) throws Exception {
		File file = new File(relative);
		check(file.isFile(), "找不到文件：" + relative);
		return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
	}

	//---- 测试用最小关卡 ----

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
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsWishPotionTest() {
	}
}
