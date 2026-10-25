/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pd.ShatteredPixelDungeon;
import pd.items.BrewRecipe.AdjuvantSet;
import pd.items.BrewRecipe.FailureTable;
import pd.items.BrewRecipe.Slots;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.consum.food.MagicCandy;
import pd.items.consum.food.MagicJuice;
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.processed.Salt;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.consum.materials.EnergyFlask;
import pd.items.consum.materials.EyeOfTruth;
import pd.items.consum.materials.Waste;
import pd.items.consum.potions.AbstractConcoction;
import pd.items.consum.potions.BenignConcoction;
import pd.items.consum.potions.ChaoticConcoction;
import pd.items.consum.potions.MalignantConcoction;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionForm;
import pd.items.consum.potions.brews.Brew;
import pd.items.consum.potions.elixirs.Elixir;
import pd.items.consum.potions.exotic.ExoticPotion;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.plants.Plant;
import pd.plants.PlantHarvest;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 新炼金体系的药水线公式（docs/alchemy-rework.md §7.1~§7.4）。
 *
 * <pre>
 * 果子 ──粗制(60%)──→ 粗制药水 ──精制·选择2(+60%)──┐
 * 果子 ──精制·选择1(40%+40%)───────────────────────┤
 *                                                   ▼
 *                            精制药水 ──合剂酿造──→ 合剂（唯一入口，裁决 #1A）
 * 紊乱魔药 ×3 ──提炼──→ 良性紊乱魔药 + 恶性紊乱魔药
 * </pre>
 *
 * <p>蔬菜→药水映射（§5.2）需要先盘点「种子 ↔ 蔬菜」对应，暂未接入。
 */
public final class SpsBrewRecipes {

	/** 果实主材的基础成功率。 */
	/** 药水粗制：主材普通果实 + 水 → 该果实的粗制药水（效果为标准药水的 1/3）。 */
	public static final class CrudeBrew extends BrewRecipe {

		public CrudeBrew() {
			//主材由 isFruitItem + potionType 判定（果实是 SpsFruit 家族，不是 Fruit 类）
			mainMaterial = null;
			//SPSEXPD: 水就是普通辅材之一，写进辅材方案（不再有 needsWater）
			adjuvantOptions = new AdjuvantSet[]{
					new AdjuvantSet(new AdjuvantSet.Slot(WaterItem.class))
			};
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			return super.matches(ingredients) && isNormalFruit(ingredients.main);
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return variant(potionType(ingredients.main), PotionForm.CRUDE);
		}
	}

	/**
	 * 标准药水：主材**大型果实** + 盐 + 水 → 该果实的标准药水（原药水的完整效果）。
	 * 取代旧的「精制」两套方案（2026-10 二次改版）。
	 */
	public static final class StandardBrew extends BrewRecipe {

		public StandardBrew() {
			//主材由 isLargeFruit + potionType 判定
			mainMaterial = null;
			//盐与水都是普通辅材
			adjuvantOptions = new AdjuvantSet[]{
					new AdjuvantSet(
							new AdjuvantSet.Slot(Salt.class),
							new AdjuvantSet.Slot(WaterItem.class))
			};
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			return super.matches(ingredients) && isLargeFruit(ingredients.main);
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return variant(potionType(ingredients.main), PotionForm.STANDARD);
		}
	}

	/**
	 * 合剂：主材**粗制药水** + 4 个辅材格中包含本局两味 → 该药水的合剂；
	 * 没有凑齐两味时退回一瓶对应粗制药水（副材消耗）。
	 * 玩家不被告知那两味是什么（后续会有揭示机制）。
	 */
	public static final class MixBrew extends BrewRecipe {

		public MixBrew() {
			mainMaterial = null;
			adjuvantOptions = null;
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			//只要有粗制药水主材 + 至少 1 个辅材就命中（产出按是否凑齐两味决定）
			return isCrude(ingredients.main) && ingredients.adjuvantCount() >= 1;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			if (SpsBrewMix.containsRecipe(ingredients)) {
				Class<? extends ExoticPotion> exotic = exoticOf(ingredients.main);
				if (exotic != null) {
					Item crafted = Reflection.newInstance(exotic);
					if (crafted != null) return crafted;
				}
			}
			//未凑齐两味：退回对应粗制药水
			return variantOf(ingredients.main, PotionForm.CRUDE);
		}
	}

	/**
	 * 魔药：主材**标准药水** + 4 个辅材格中包含本局两味 → 该药水对应的魔药；
	 * 没有凑齐两味时退回一瓶对应粗制药水（副材消耗）。
	 */
	public static final class ElixirBrew extends BrewRecipe {

		public ElixirBrew() {
			mainMaterial = null;
			adjuvantOptions = null;
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			return SpsBrewMix.isStandard(ingredients.main) && ingredients.adjuvantCount() >= 1;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			if (SpsBrewMix.containsRecipe(ingredients)) {
				Item brewed = SpsBrewMix.brewElixir(ingredients.main);
				if (brewed != null) return brewed;
			}
			//未凑齐两味：退回对应粗制药水
			return variantOf(ingredients.main, PotionForm.CRUDE);
		}
	}

	//SPSEXPD: 「提炼」（3 瓶紊乱魔药 → 良性 + 恶性）随成功率/失败产物一并取消（2026-10 裁决）：
	//紊乱魔药不再有产出途径，提炼公式整条移除（物品类保留，旧档兼容）。

	/**
	 * 结晶：鉴定卷轴 + 水 + 火源 → 真实之眼（§7.7 XR-01，不失败、免能量）。
	 */
	public static final class EyeCrystal extends BrewRecipe {

		public EyeCrystal() {
			mainMaterial = ScrollOfIdentify.class;
			//水作为普通辅材
			adjuvantOptions = new AdjuvantSet[]{
					new AdjuvantSet(new AdjuvantSet.Slot(WaterItem.class))
			};
			energyCost = 0;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return new EyeOfTruth();
		}
	}

	//SPSEXPD: 旧的「缓释片 / 注射剂 / 试剂」一步公式（TabletBrew / InjectorBrew / ReagentBrew）
	//随二次改版删除（docs/alchemy-rework.md §7.5）：缓释片改由「蒸馏 → 药片 → +盐 → 缓释片×2」，
	//注射剂/试剂被安培取代。物品与形态保留供旧档兼容。

	/**
	 * 炼金能量瓶：水 + 4 点炼金能量 → 能量瓶（§6.5，特殊公式保留能量消耗）。
	 * 水放在主材格，不需要额外的水/火源。
	 */
	public static final class EnergyFlaskBrew extends BrewRecipe {

		public EnergyFlaskBrew() {
			mainMaterial = WaterItem.class;
			adjuvantOptions = null;
			energyCost = 4;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return new EnergyFlask();
		}
	}

	/**
	 * 魔法果汁：主材蔬菜（魔法蔬菜）+ 辅材任意果实 + 水 → 魔法果汁。
	 *
	 * <p>SPSEXPD: 7 种蔬菜的效果分别对应旧菌类改造来的果汁瓶，直接产出那些瓶子；其余蔬菜产普通果汁。
	 */
	public static final class MagicJuiceBrew extends BrewRecipe {


		public MagicJuiceBrew() {
			mainMaterial = Vegetable.class;
			//果实与水都是普通辅材
			adjuvantOptions = new AdjuvantSet[]{
					new AdjuvantSet(
							new AdjuvantSet.Slot(MissileWeapon.class, SpsBrewRecipes::isFruitItem),
							new AdjuvantSet.Slot(WaterItem.class))
			};
			energyCost = 0;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			//SPSEXPD: 果汁按蔬菜分——7 种效果对应的蔬菜产旧菌类改造来的果汁瓶，其余产普通蔬菜果汁
			Class<? extends Item> veg = ingredients.main == null ? null : ingredients.main.getClass();
			return pd.items.consum.food.MagicJuice.produce(veg);
		}
	}

	/**
	 * SPSEXPD: 单蔬菜汁——萝卜 / 松露 / 莴苣 / 苹果这些本身就是食材的蔬菜，不用水和果实，
	 * 单独放进炼金釜就能榨汁。
	 */
	public static final class SimpleJuiceBrew extends BrewRecipe {

		private final Class<? extends Item> vegetable;

		public SimpleJuiceBrew(Class<? extends Item> vegetable) {
			this.vegetable = vegetable;
			mainMaterial = vegetable;
			//零辅材：只要主材
			adjuvantOptions = new AdjuvantSet[0];
			energyCost = 0;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return pd.items.consum.food.MagicJuice.of(vegetable);
		}
	}

	//*** 再加工（蒸馏压片机 / 分装灌液机）

	/**
	 * 蒸馏：魔法果汁 → 魔法硬糖；粗制/标准/合剂/魔药 → 同名药片；药片 + 盐 → 缓释片 ×2。
	 * 只在「蒸馏压片机」界面里出现（见 AlchemyScene.spsMode）。
	 */
	public static final class DistillBrew extends BrewRecipe {

		public DistillBrew() {
			mainMaterial = null;
			adjuvantOptions = null;
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			Item main = ingredients.main;
			if (main == null) return false;
			if (main instanceof MagicJuice) return ingredients.adjuvantCount() == 0;
			if (!isProcessable(main)) return false;
			if (((Potion) main).form == PotionForm.PILL) {
				//药片 + 盐 → 缓释片
				if (ingredients.adjuvantCount() != 1) return false;
				for (Item item : ingredients.adjuvant) {
					if (item instanceof Salt) return true;
				}
				return false;
			}
			return ingredients.adjuvantCount() == 0;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			Item main = ingredients.main;
			if (main instanceof MagicJuice) {
				//SPSEXPD: 硬糖同样按来源分——普通果汁用它的蔬菜类，旧瓶果汁用它自身的类
				Class<? extends Item> src = ((MagicJuice) main).vegetable;
				return pd.items.consum.food.MagicCandy.of(src != null ? src : main.getClass());
			}
			Potion potion = (Potion) main;
			if (potion.form == PotionForm.PILL) {
				Potion tablet = variantAny(potion, PotionForm.TABLET);
				if (tablet != null) tablet.quantity(2);
				return tablet;
			}
			return variantAny(potion, PotionForm.PILL);
		}
	}

	/**
	 * 分装：粗制/标准/合剂/魔药 + 水 + 盐 → 同名安培 ×3（需配合投掷注射针使用）。
	 * 只在「分装灌液机」界面里出现。
	 */
	public static final class FillBrew extends BrewRecipe {

		public FillBrew() {
			mainMaterial = null;
			//盐与水都是普通辅材
			adjuvantOptions = new AdjuvantSet[]{
					new AdjuvantSet(
							new AdjuvantSet.Slot(Salt.class),
							new AdjuvantSet.Slot(WaterItem.class))
			};
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			if (!isProcessable(ingredients.main)) return false;
			if (((Potion) ingredients.main).form == PotionForm.PILL) return false;
			return super.matches(ingredients);
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			Potion ampoule = variantAny(ingredients.main, PotionForm.AMPOULE);
			if (ampoule != null) ampoule.quantity(3);
			return ampoule;
		}
	}

	/** 可再加工的药剂：粗制 / 标准 / 合剂 / 魔药（药片是蒸馏中间体，单独判定）。
	 * SPSEXPD: 排除任务/专属道具与调试用的测试药剂。 */
	public static boolean isProcessable(Item item) {
		if (!(item instanceof Potion)) return false;
		if (Recipe.isSpecialItem(item)) return false;
		if (item.getClass().getSimpleName().contains("Test")) return false;
		PotionForm form = ((Potion) item).form;
		if (form == PotionForm.CRUDE || form == PotionForm.STANDARD || form == PotionForm.PILL) return true;
		return item instanceof ExoticPotion || item instanceof Brew;
	}

	/** 构建任意药水的指定形态（含合剂/魔药；不比 isPlainPotion 更严格）。 */
	@SuppressWarnings("unchecked")
	public static Potion variantAny(Item main, PotionForm form) {
		if (!(main instanceof Potion)) return null;
		Potion potion = Reflection.newInstance((Class<? extends Potion>) main.getClass());
		if (potion != null) potion.form = form;
		return potion;
	}

	//*** 工具

	/** 主材是否为「普通药剂」（排除合剂/秘药/酿造物/紊乱魔药家族）。 */
	public static boolean isPlainPotion(Item item) {
		return item instanceof Potion
				&& !(item instanceof ExoticPotion)
				&& !(item instanceof Elixir)
				&& !(item instanceof Brew)
				&& !(item instanceof AbstractConcoction);
	}

	/** 构建同种药水的指定形态（形态转换类公式用）。 */
	public static Potion variantOf(Item main, PotionForm form) {
		if (!isPlainPotion(main)) return null;
		Potion potion = Reflection.newInstance((Class<? extends Potion>) main.getClass());
		if (potion != null) potion.form = form;
		return potion;
	}

	/** 主材（果实或蔬菜）对应的药剂类；无法映射时返回 null（不参与配方）。 */
	public static Class<? extends Potion> potionType(Item main) {
		if (main == null) return null;
		if (main instanceof Vegetable) return vegetableToPotion().get(main.getClass());
		return Potion.FruitToPotion.potionFor(main);
	}

	/**
	 * SPSEXPD: 蔬菜 → 药剂映射（docs/alchemy-rework.md §5.2 / 裁决 #4）。
	 *
	 * <p>唯一真源是「种子 ↔ 蔬菜」：{@link pd.plants.PlantHarvest} 里每种植物产出哪种蔬菜，
	 * 再看该植物的种子在 {@code Potion.SeedToPotion.types} 里对应哪种药剂（「种什么收什么药」）。
	 * 没有种子映射的蔬菜（如无味果）不参与精制主材池（裁决 #4B）。
	 */
	private static Map<Class<? extends Item>, Class<? extends Potion>> vegetableMap;

	private static Map<Class<? extends Item>, Class<? extends Potion>> vegetableToPotion() {
		if (vegetableMap == null) {
			Map<Class<? extends Item>, Class<? extends Potion>> map = new HashMap<>();
			for (Map.Entry<Class<? extends Plant.Seed>, Class<? extends Potion>> entry
					: Potion.SeedToPotion.types.entrySet()) {
				Class<? extends Item> vegetable = PlantHarvest.vegetableForSeed(entry.getKey());
				//同一蔬菜可能有多个来源种子：先出现的映射优先（例如榴莲取力量药剂）
				if (vegetable != null && !map.containsKey(vegetable)) {
					map.put(vegetable, entry.getValue());
				}
			}
			vegetableMap = map;
		}
		return vegetableMap;
	}

	public static boolean isCrude(Item item) {
		return item instanceof Potion && ((Potion) item).form == PotionForm.CRUDE;
	}

	/** SPSEXPD: 是否为投掷果实（SpsFruit 家族——炼金药水的果实主材）。 */
	public static boolean isFruitItem(Item item) {
		return item instanceof MissileWeapon && ((MissileWeapon) item).isFruit();
	}

	/** 是否可酿药水的普通果实（大果不算）。 */
	public static boolean isNormalFruit(Item item) {
		return isFruitItem(item) && Potion.FruitToPotion.potionFor(item) != null
				&& !Potion.FruitToPotion.isLarge(item);
	}

	/** 是否可酿药水的大型果实（标准药水的主材）。 */
	public static boolean isLargeFruit(Item item) {
		return isFruitItem(item) && Potion.FruitToPotion.isLarge(item);
	}

	/** 主材药剂对应的合剂类（无对应则 null，配方不匹配）。 */
	public static Class<? extends ExoticPotion> exoticOf(Item main) {
		if (!(main instanceof Potion)) return null;
		return ExoticPotion.regToExo.get(main.getClass());
	}

	/** 构建某药水类的指定形态实例（贴图暂沿用原药水帧，裁决 #9）。 */
	public static Potion variant(Class<? extends Potion> type, PotionForm form) {
		if (type == null) return null;
		Potion potion = Reflection.newInstance(type);
		if (potion != null) potion.form = form;
		return potion;
	}

	public static Item randomExotic() {
		Object[] types = ExoticPotion.regToExo.values().toArray();
		if (types.length == 0) return new ChaoticConcoction();
		@SuppressWarnings("unchecked")
		Class<? extends ExoticPotion> type = (Class<? extends ExoticPotion>) types[Random.Int(types.length)];
		ExoticPotion potion = Reflection.newInstance(type);
		return potion == null ? new ChaoticConcoction() : potion;
	}

	/** 全部炼金公式（顺序即匹配顺序）。
	 * SPSEXPD: 优先读 assets/data/alchemy.csv（表驱动，方便平衡调整）；
	 * 表格缺失或解析失败时回退到内置公式。再加工（蒸馏/分装）公式不在表里，始终追加。 */
	public static List<Recipe> all() {
		List<Recipe> recipes = new ArrayList<>();
		List<BrewRecipe> fromTable = AlchemyCsv.load();
		if (!fromTable.isEmpty()) {
			recipes.addAll(fromTable);
		} else {
			recipes.addAll(builtin());
		}
		//SPSEXPD: 果汁公式必须始终注册——CSV 的 ITEM 规则只能反射 new 出通用物品，
		//传不了「是哪一种蔬菜/浆果」，所以按来源区分的果汁只能由内置公式产出。
		recipes.addAll(juiceRecipes());
		//蒸馏压片机 / 分装灌液机 专用公式（由 AlchemyScene.spsMode 过滤）
		recipes.add(new DistillBrew());
		recipes.add(new FillBrew());
		return recipes;
	}

	/**
	 * SPSEXPD: 果汁公式（不随 CSV 有无而变）。
	 *
	 * <ul>
	 *   <li>{@link MagicJuiceBrew}：蔬菜 + 任意果实 + 水</li>
	 *   <li>{@link SimpleJuiceBrew}：萝卜 / 松露 / 莴苣 / 苹果 / 各种浆果，单放主材即可</li>
	 * </ul>
	 */
	public static List<Recipe> juiceRecipes() {
		List<Recipe> recipes = new ArrayList<>();
		recipes.add(new MagicJuiceBrew());
		recipes.add(new SimpleJuiceBrew(pd.items.consum.food.vegetable.Radish.class));
		recipes.add(new SimpleJuiceBrew(pd.items.consum.food.vegetable.Truffles.class));
		recipes.add(new SimpleJuiceBrew(pd.items.consum.food.vegetable.Lettuce.class));
		recipes.add(new SimpleJuiceBrew(pd.items.consum.food.vegetable.Apple.class));
		//五色浆果：每种都能直接榨成浆果汁
		Class<?>[] berries = Generator.Category.SPS_BERRY.classes;
		if (berries != null) {
			for (Class<?> berry : berries) {
				if (berry != null && Item.class.isAssignableFrom(berry)) {
					//noinspection unchecked
					recipes.add(new SimpleJuiceBrew((Class<? extends Item>) berry));
				}
			}
		}
		return recipes;
	}

	/** 内置公式：CSV 不可用时的兜底，数值应与 assets/data/alchemy.csv 保持一致。
	 * 注意：秘药并入（FIXED 行）只在 CSV 里，兜底缺这一段（CSV 是主路径）。 */
	public static List<Recipe> builtin() {
		return Arrays.asList(
				new EnergyFlaskBrew(),
				new CrudeBrew(),
				new StandardBrew(),
				new MixBrew(),
				new ElixirBrew(),
				//SPSEXPD: 果汁公式（MagicJuiceBrew + SimpleJuiceBrew 各种）统一由 juiceRecipes() 提供，
				//在 all() 里始终追加，见那里的注释
				new EyeCrystal()
		);
	}

	private SpsBrewRecipes() {
	}
}
