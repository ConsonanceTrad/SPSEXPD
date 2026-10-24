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
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.vegetable.Vegetable;
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
	/** 药水粗制：主材果子 + 辅材果子 + 水 → 该果实的粗制药水。 */
	public static final class CrudeBrew extends BrewRecipe {

		public CrudeBrew() {
			mainMaterial = Fruit.class;
			adjuvantOptions = new AdjuvantSet[]{
					new AdjuvantSet(new AdjuvantSet.Slot(Fruit.class))
			};
			needsWater = true;
			//SPSEXPD: 基础药水配方不再需要火——火源（火种/磷火）预留给烹饪配方
			needsFire = false;
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			return super.matches(ingredients) && potionType(ingredients.main) != null;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return variant(potionType(ingredients.main), PotionForm.CRUDE);
		}
	}

	/**
	 * 药水精制：主材果子（40%）或蔬菜（60%）+ 两种辅材方案（2 果子各 +20%，或 1 瓶粗制药水 +60%）。
	 * 成功产出精制药水，失败固定产出紊乱魔药。
	 */
	public static final class RefinedBrew extends BrewRecipe {

		public RefinedBrew() {
			//主材为果子或蔬菜，由 potionType 判定
			mainMaterial = null;
			adjuvantOptions = new AdjuvantSet[]{
					new AdjuvantSet(
							new AdjuvantSet.Slot(Fruit.class),
							new AdjuvantSet.Slot(Fruit.class)),
					new AdjuvantSet(
							new AdjuvantSet.Slot(Potion.class, SpsBrewRecipes::isCrude))
			};
			needsWater = true;
			//SPSEXPD: 基础药水配方不再需要火——火源（火种/磷火）预留给烹饪配方
			needsFire = false;
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			return super.matches(ingredients) && potionType(ingredients.main) != null;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return variant(potionType(ingredients.main), PotionForm.REFINED);
		}
	}

	/**
	 * 合剂酿造：主材目标药剂 + 1 瓶其他药剂（+40%）或 2 份其他素材（+10%+10%）+ 水 + 火源。
	 * 成功产出 regToExo 映射的合剂；失败 70% 紊乱 / 5% 随机合剂 / 25% 废料。
	 */
	public static final class MixBrew extends BrewRecipe {

		public MixBrew() {
			mainMaterial = Potion.class;
			adjuvantOptions = new AdjuvantSet[]{
					new AdjuvantSet(new AdjuvantSet.Slot(Potion.class)),
					new AdjuvantSet(
							new AdjuvantSet.Slot(Item.class),
							new AdjuvantSet.Slot(Item.class))
			};
			needsWater = true;
			//SPSEXPD: 基础药水配方不再需要火——火源（火种/磷火）预留给烹饪配方
			needsFire = false;
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			return super.matches(ingredients) && exoticOf(ingredients.main) != null;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			Class<? extends ExoticPotion> exotic = exoticOf(ingredients.main);
			return exotic == null ? null : Reflection.newInstance(exotic);
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
			adjuvantOptions = null;
			needsWater = true;
			//SPSEXPD: 基础炼金配方不再需要火——火源（火种/磷火）预留给烹饪配方
			needsFire = false;
			energyCost = 0;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return new EyeOfTruth();
		}
	}

	/**
	 * 缓释片：目标药剂 → 同种药水的缓释片（免回合吞服、效果更持久、**没有投掷效果**）。
	 * 不失败、免水、免火源、免能量。
	 */
	public static final class TabletBrew extends BrewRecipe {

		public TabletBrew() {
			mainMaterial = Potion.class;
			adjuvantOptions = null;
			needsWater = false;
			needsFire = false;
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			return super.matches(ingredients) && isPlainPotion(ingredients.main);
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return variantOf(ingredients.main, PotionForm.TABLET);
		}
	}

	/**
	 * 注射剂：目标药剂 + 水 → 同种药水的注射剂
	 * （免回合使用、可投掷、药性更集中猛烈）。
	 */
	public static final class InjectorBrew extends BrewRecipe {

		public InjectorBrew() {
			mainMaterial = Potion.class;
			adjuvantOptions = null;
			needsWater = true;
			needsFire = false;
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			return super.matches(ingredients) && isPlainPotion(ingredients.main);
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return variantOf(ingredients.main, PotionForm.INJECTOR);
		}
	}

	/**
	 * 试剂：目标药剂 + 水 → 2~4 瓶试剂（效果为正常的 1/3，供魔法药瓶自动喂食）。
	 */
	public static final class ReagentBrew extends BrewRecipe {

		public ReagentBrew() {
			mainMaterial = Potion.class;
			adjuvantOptions = null;
			needsWater = true;
			needsFire = false;
			energyCost = 0;
		}

		@Override
		public boolean matches(Slots ingredients) {
			return super.matches(ingredients) && isPlainPotion(ingredients.main);
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			Potion reagent = variantOf(ingredients.main, PotionForm.REAGENT);
			if (reagent != null) reagent.quantity(Random.IntRange(2, 4));
			return reagent;
		}
	}

	/**
	 * 炼金能量瓶：水 + 4 点炼金能量 → 能量瓶（§6.5，特殊公式保留能量消耗）。
	 * 水放在主材格，不需要额外的水/火源。
	 */
	public static final class EnergyFlaskBrew extends BrewRecipe {

		public EnergyFlaskBrew() {
			mainMaterial = WaterItem.class;
			adjuvantOptions = null;
			needsWater = false;
			needsFire = false;
			energyCost = 4;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			return new EnergyFlask();
		}
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
	 * 表格缺失或解析失败时回退到下面的内置公式（与 CSV 同值）。 */
	public static List<Recipe> all() {
		List<BrewRecipe> fromTable = AlchemyCsv.load();
		if (!fromTable.isEmpty()) {
			List<Recipe> recipes = new ArrayList<>(fromTable.size());
			recipes.addAll(fromTable);
			return recipes;
		}
		return builtin();
	}

	/** 内置公式：CSV 不可用时的兜底，数值应与 assets/data/alchemy.csv 保持一致。 */
	public static List<Recipe> builtin() {
		return Arrays.asList(
				new EnergyFlaskBrew(),
				new TabletBrew(),
				new InjectorBrew(),
				new ReagentBrew(),
				new CrudeBrew(),
				new RefinedBrew(),
				new MixBrew(),
				new EyeCrystal()
		);
	}

	private SpsBrewRecipes() {
	}
}
