/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

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
import pd.items.consum.materials.Waste;
import pd.items.consum.potions.BenignConcoction;
import pd.items.consum.potions.ChaoticConcoction;
import pd.items.consum.potions.MalignantConcoction;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionForm;
import pd.items.consum.potions.exotic.ExoticPotion;
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
	private static final int FRUIT_RATE = 40;
	/** 蔬菜主材的基础成功率（精制·选择1/2）。 */
	private static final int VEGETABLE_RATE = 60;
	/** 一个果实辅材的成功率加成。 */
	private static final int FRUIT_BONUS = 20;
	/** 一个粗制药水辅材的成功率加成（精制·选择2）。 */
	private static final int CRUDE_BONUS = 60;
	/** 合剂的一个「其他药水」辅材加成 / 两个「其他素材」辅材加成。 */
	private static final int MIX_POTION_BONUS = 40;
	private static final int MIX_MATERIAL_BONUS = 10;

	/** 药水粗制：主材果子 + 辅材果子 + 水 + 火源 → 该果实的粗制药水。 */
	public static final class CrudeBrew extends BrewRecipe {

		public CrudeBrew() {
			mainMaterial = Fruit.class;
			adjuvantOptions = new AdjuvantSet[]{
					new AdjuvantSet(new AdjuvantSet.Slot(Fruit.class, FRUIT_BONUS))
			};
			needsWater = true;
			needsFire = true;
			energyCost = 0;
			baseRate = FRUIT_RATE;
			//75% 紊乱魔药 / 25% 废料
			failures = new FailureTable(new int[]{75, 25},
					ChaoticConcoction::new, Waste::new);
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
							new AdjuvantSet.Slot(Fruit.class, FRUIT_BONUS),
							new AdjuvantSet.Slot(Fruit.class, FRUIT_BONUS)),
					new AdjuvantSet(
							new AdjuvantSet.Slot(Potion.class, CRUDE_BONUS, SpsBrewRecipes::isCrude))
			};
			needsWater = true;
			needsFire = true;
			energyCost = 0;
			baseRate = FRUIT_RATE;
			failures = FailureTable.single(ChaoticConcoction::new);
		}

		@Override
		protected int baseRate(Item main) {
			//蔬菜主材提供 60% 基础成功率
			return main instanceof Vegetable ? VEGETABLE_RATE : FRUIT_RATE;
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
					new AdjuvantSet(new AdjuvantSet.Slot(Potion.class, MIX_POTION_BONUS)),
					new AdjuvantSet(
							new AdjuvantSet.Slot(Item.class, MIX_MATERIAL_BONUS),
							new AdjuvantSet.Slot(Item.class, MIX_MATERIAL_BONUS))
			};
			needsWater = true;
			needsFire = true;
			energyCost = 0;
			//主材本身不提供成功率，全靠辅材
			baseRate = 0;
			failures = new FailureTable(new int[]{70, 5, 25},
					ChaoticConcoction::new, SpsBrewRecipes::randomExotic, Waste::new);
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

	/**
	 * 提炼（分离）：3 瓶紊乱魔药 → 1 瓶良性 + 1 瓶恶性。
	 * 不失败、免水、免火源、免能量。
	 */
	public static final class SeparateBrew extends BrewRecipe {

		public SeparateBrew() {
			mainMaterial = ChaoticConcoction.class;
			adjuvantOptions = new AdjuvantSet[]{
					new AdjuvantSet(
							new AdjuvantSet.Slot(ChaoticConcoction.class, 0),
							new AdjuvantSet.Slot(ChaoticConcoction.class, 0))
			};
			needsWater = false;
			needsFire = false;
			energyCost = 0;
			baseRate = 100;
			failures = null;
		}

		@Override
		protected Item successOutput(Slots ingredients) {
			extraOutputs.add(new MalignantConcoction());
			return new BenignConcoction();
		}
	}

	//*** 工具

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

	private static Item randomExotic() {
		Object[] types = ExoticPotion.regToExo.values().toArray();
		if (types.length == 0) return new ChaoticConcoction();
		@SuppressWarnings("unchecked")
		Class<? extends ExoticPotion> type = (Class<? extends ExoticPotion>) types[Random.Int(types.length)];
		ExoticPotion potion = Reflection.newInstance(type);
		return potion == null ? new ChaoticConcoction() : potion;
	}

	/** 全部药水线公式（顺序即匹配顺序）。 */
	public static List<Recipe> all() {
		return Arrays.asList(
				new CrudeBrew(),
				new RefinedBrew(),
				new MixBrew(),
				new SeparateBrew()
		);
	}

	private SpsBrewRecipes() {
	}
}
