/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.WaterItem;
import pd.items.consum.materials.EnergyFlask;
import pd.items.consum.materials.Tinder;
import pd.items.consum.materials.WillOWisp;
import pd.items.consum.potions.Potion;
import render.utils.math.Random;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * SPSEXPD: 新炼金框架——材料公式「主材 + ≤4 辅材 + 水 + 火源」。
 *
 * <p>与旧的 {@link SpsAlchemyRecipes.TypedRecipe} 的区别：
 * <ul>
 *   <li>槽位带语义：主材 / 辅材 / 水 / 火源，水与火源必须与公式声明一致；</li>
 *   <li>辅材支持多套方案（选择1 / 选择2），每套的每个槽位自带成功率加成；</li>
 *   <li>带成功率与失败产物分布（紊乱魔药 / 废料 / 随机合剂等），掷点在 {@link #brew} 统一进行；</li>
 *   <li>能量成本默认 0，只有特殊公式（炼金能量瓶、提炼液金）才非 0。</li>
 * </ul>
 *
 * <p>设计文档：{@code docs/alchemy-rework.md} §2。
 */
public abstract class BrewRecipe extends Recipe {

	/** 辅材槽上限（炼金釜 UI 的 2×2 两行）。 */
	public static final int ADJUVANT_SLOTS = 4;

	//*** 子类填充项
	protected Class<? extends Item> mainMaterial;   // 主材（基类 = 任意子类）
	protected AdjuvantSet[] adjuvantOptions;        // 辅材方案（多选一，可含「无辅材」方案）
	protected int energyCost;
	//***

	/** 炼金釜 5 格的语义容器，也可由物品列表推断（{@link #of(ArrayList)}）。
	 * SPSEXPD: 水与火种不再是特殊槽——它们就是普通辅材，按顺序进 {@link #adjuvant}。 */
	public static final class Slots {

		public Item main;
		public final Item[] adjuvant = new Item[ADJUVANT_SLOTS];

		/** 由「所有非空槽的物品」推断语义槽：第一个 → 主材，其余按顺序 → 辅材。 */
		public static Slots of(ArrayList<Item> ingredients) {
			Slots slots = new Slots();
			if (ingredients == null) return slots;
			for (Item item : ingredients) {
				if (item == null) continue;
				if (slots.main == null) {
					slots.main = item;
				} else {
					for (int i = 0; i < ADJUVANT_SLOTS; i++) {
						if (slots.adjuvant[i] == null) {
							slots.adjuvant[i] = item;
							break;
						}
					}
				}
			}
			return slots;
		}

		public int adjuvantCount() {
			int count = 0;
			for (Item item : adjuvant) {
				if (item != null) count++;
			}
			return count;
		}

		public boolean isEmpty() {
			return main == null && adjuvantCount() == 0;
		}

		public ArrayList<Item> all() {
			ArrayList<Item> result = new ArrayList<>();
			if (main != null) result.add(main);
			for (Item item : adjuvant) {
				if (item != null) result.add(item);
			}
			return result;
		}
	}

	/** 火源：火种或磷火（仅作材料类型判定用，不再是「特殊槽」）。 */
	public static boolean isFireSource(Item item) {
		return item instanceof Tinder || item instanceof WillOWisp;
	}

	/**
	 * 一套辅材方案：槽位数量与类型都必须精确匹配（多放辅材不命中）。
	 */
	public static final class AdjuvantSet {

		public final Slot[] slots;

		public AdjuvantSet(Slot... slots) {
			this.slots = slots;
		}

		public boolean matches(Slots ingredients) {
			if (ingredients.adjuvantCount() != slots.length) return false;
			return matchFrom(ingredients, 0, new boolean[ADJUVANT_SLOTS]);
		}

		private boolean matchFrom(Slots ingredients, int slot, boolean[] used) {
			if (slot == slots.length) return true;
			for (int i = 0; i < ADJUVANT_SLOTS; i++) {
				Item item = ingredients.adjuvant[i];
				if (item != null && !used[i] && slots[slot].accepts(item)) {
					used[i] = true;
					if (matchFrom(ingredients, slot + 1, used)) return true;
					used[i] = false;
				}
			}
			return false;
		}

		public static final class Slot {
			public final Class<? extends Item> type;
			/** 额外的接受条件（可为 null，例如「粗制药水」= Potion + form==CRUDE）。 */
			public final Predicate<Item> filter;

			public Slot(Class<? extends Item> type) {
				this(type, null);
			}

			public Slot(Class<? extends Item> type, Predicate<Item> filter) {
				this.type = type;
				this.filter = filter;
			}

			public boolean accepts(Item item) {
				return type.isInstance(item) && (filter == null || filter.test(item));
			}
		}
	}

	/** 失败产物分布：权重 → 产物。 */
	public static final class FailureTable {

		private final int[] weights;
		private final Supplier<Item>[] outputs;

		@SafeVarargs
		public FailureTable(int[] weights, Supplier<Item>... outputs) {
			this.weights = weights;
			this.outputs = outputs;
		}

		public static FailureTable single(Supplier<Item> output) {
			return new FailureTable(new int[]{1}, output);
		}

		public Item roll() {
			int total = 0;
			for (int weight : weights) {
				total += weight;
			}
			if (total <= 0) return null;
			int roll = Random.Int(total);
			for (int i = 0; i < weights.length; i++) {
				roll -= weights[i];
				if (roll < 0) return outputs[i].get();
			}
			return outputs[outputs.length - 1].get();
		}
	}

	//*** 匹配

	@Override
	public boolean testIngredients(ArrayList<Item> ingredients) {
		return matches(Slots.of(ingredients));
	}

	public boolean matches(Slots ingredients) {
		if (ingredients.main == null) return false;
		//mainMaterial 为 null 时由子类自己判定主材（例如粗制接受任意果实）
		if (mainMaterial != null && !mainMaterial.isInstance(ingredients.main)) return false;

		//SPSEXPD: 水与火种就是普通辅材——只按辅材方案精确匹配，不再有 needsWater/needsFire
		if (adjuvantOptions == null || adjuvantOptions.length == 0) {
			return ingredients.adjuvantCount() == 0;
		}
		return matchAdjuvant(ingredients) != null;
	}

	/** 命中的辅材方案（多套都命中时取第一套命中的）。 */
	public AdjuvantSet matchAdjuvant(Slots ingredients) {
		if (adjuvantOptions == null) return null;
		for (AdjuvantSet option : adjuvantOptions) {
			if (option.matches(ingredients)) return option;
		}
		return null;
	}

	//*** 产出

	@Override
	public int cost(ArrayList<Item> ingredients) {
		return energyCost;
	}

	/** SPSEXPD: 全部公式 100% 成功、没有失败产物（2026-10 裁决）。 */
	@Override
	public Item brew(ArrayList<Item> ingredients) {
		Slots slots = Slots.of(ingredients);
		if (!matches(slots)) return null;

		extraOutputs.clear();
		for (Item ingredient : ingredients) {
			ingredient.quantity(ingredient.quantity() - 1);
		}
		return successOutput(slots);
	}

	/** 一次合成的额外产物（例如提炼：良性 + 恶性各一瓶），成功时由 successOutput 填充。 */
	protected final ArrayList<Item> extraOutputs = new ArrayList<>();

	public ArrayList<Item> takeExtraOutputs() {
		ArrayList<Item> copy = new ArrayList<>(extraOutputs);
		extraOutputs.clear();
		return copy;
	}

	@Override
	public Item sampleOutput(ArrayList<Item> ingredients) {
		Slots slots = Slots.of(ingredients);
		if (!matches(slots)) return null;
		return successOutput(slots);
	}

	/** 成功产物（预览与真正产出共用）。 */
	protected abstract Item successOutput(Slots ingredients);
}
