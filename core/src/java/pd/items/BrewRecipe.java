/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.WaterItem;
import pd.items.consum.materials.Tinder;
import pd.items.consum.materials.WillOWisp;
import pd.items.consum.potions.Potion;
import render.utils.math.Random;

import java.util.ArrayList;
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

	/** 磷火作火源时的成功率加成（裁决 #7）。 */
	public static final int WILL_O_WISP_BONUS = 5;
	/** 大小果实混用的成功率惩罚（裁决 #10）。 */
	public static final int MIXED_FRUIT_PENALTY = 10;

	//*** 子类填充项
	protected Class<? extends Item> mainMaterial;   // 主材（基类 = 任意子类）
	protected AdjuvantSet[] adjuvantOptions;        // 辅材方案（多选一，可含「无辅材」方案）
	protected boolean needsWater;
	protected boolean needsFire;
	protected int energyCost;
	protected int baseRate = 100;                   // 主材本身提供的成功率（%）
	protected FailureTable failures;                // 失败产物分布（null = 不会失败）
	//***

	/** 炼金釜 7 格的语义容器，也可由物品列表推断（{@link #of(ArrayList)}）。 */
	public static final class Slots {

		public Item main;
		public final Item[] adjuvant = new Item[ADJUVANT_SLOTS];
		public Item water;
		public Item fire;

		/** 由「所有非空槽的物品」推断语义槽：水 → 水槽，火种/磷火 → 火源槽，其余按顺序主材 + 辅材。 */
		public static Slots of(ArrayList<Item> ingredients) {
			Slots slots = new Slots();
			if (ingredients == null) return slots;
			for (Item item : ingredients) {
				if (item == null) continue;
				if (item instanceof WaterItem && slots.water == null) {
					slots.water = item;
					continue;
				}
				if (isFireSource(item) && slots.fire == null) {
					slots.fire = item;
					continue;
				}
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

		public boolean hasWater() {
			return water != null;
		}

		public boolean hasFire() {
			return fire != null;
		}

		public int adjuvantCount() {
			int count = 0;
			for (Item item : adjuvant) {
				if (item != null) count++;
			}
			return count;
		}

		public boolean isEmpty() {
			return main == null && adjuvantCount() == 0 && !hasWater() && !hasFire();
		}

		public ArrayList<Item> all() {
			ArrayList<Item> result = new ArrayList<>();
			if (main != null) result.add(main);
			for (Item item : adjuvant) {
				if (item != null) result.add(item);
			}
			if (water != null) result.add(water);
			if (fire != null) result.add(fire);
			return result;
		}
	}

	/** 火源：火种或磷火。 */
	public static boolean isFireSource(Item item) {
		return item instanceof Tinder || item instanceof WillOWisp;
	}

	/**
	 * 一套辅材方案：槽位数量与类型都必须精确匹配（多放辅材不命中），
	 * 每个槽位自带成功率加成。
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
				if (item != null && !used[i] && slots[slot].type.isInstance(item)) {
					used[i] = true;
					if (matchFrom(ingredients, slot + 1, used)) return true;
					used[i] = false;
				}
			}
			return false;
		}

		public int bonus() {
			int bonus = 0;
			for (Slot slot : slots) {
				bonus += slot.bonus;
			}
			return bonus;
		}

		public static final class Slot {
			public final Class<? extends Item> type;
			public final int bonus;

			public Slot(Class<? extends Item> type, int bonus) {
				this.type = type;
				this.bonus = bonus;
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
		if (mainMaterial == null || ingredients.main == null) return false;
		if (!mainMaterial.isInstance(ingredients.main)) return false;
		if (needsWater != ingredients.hasWater()) return false;
		if (needsFire != ingredients.hasFire()) return false;

		if (adjuvantOptions == null || adjuvantOptions.length == 0) {
			return ingredients.adjuvantCount() == 0;
		}
		return matchAdjuvant(ingredients) != null;
	}

	/** 命中的辅材方案（多套都命中时取成功率最高的那套）。 */
	public AdjuvantSet matchAdjuvant(Slots ingredients) {
		AdjuvantSet best = null;
		if (adjuvantOptions == null) return null;
		for (AdjuvantSet option : adjuvantOptions) {
			if (option.matches(ingredients) && (best == null || option.bonus() > best.bonus())) {
				best = option;
			}
		}
		return best;
	}

	//*** 成功率

	/** 该次合成的最终成功率（含辅材加成、磷火加成、大小果混用惩罚）。 */
	public int successRate(ArrayList<Item> ingredients) {
		return successRate(Slots.of(ingredients));
	}

	public int successRate(Slots ingredients) {
		int rate = baseRate;
		AdjuvantSet set = matchAdjuvant(ingredients);
		if (set != null) rate += set.bonus();
		if (ingredients.fire instanceof WillOWisp) rate += WILL_O_WISP_BONUS;
		//TODO 批4：辅材里放入炼金能量瓶时 +15
		if (mixesFruitSizes(ingredients)) rate -= MIXED_FRUIT_PENALTY;
		return Math.max(1, Math.min(100, rate));
	}

	/** 是否可失败（有失败产物分布）。 */
	public boolean isRisky() {
		return failures != null;
	}

	/** 同一次合成里同时出现大型果实与普通果实（裁决 #10）。 */
	protected static boolean mixesFruitSizes(Slots ingredients) {
		boolean large = false;
		boolean normal = false;
		for (Item item : ingredients.all()) {
			if (!(item instanceof Fruit)) continue;
			if (Potion.FruitToPotion.isLarge(item)) {
				large = true;
			} else {
				normal = true;
			}
		}
		return large && normal;
	}

	//*** 产出

	@Override
	public int cost(ArrayList<Item> ingredients) {
		return energyCost;
	}

	@Override
	public Item brew(ArrayList<Item> ingredients) {
		Slots slots = Slots.of(ingredients);
		if (!matches(slots)) return null;

		boolean success = failures == null || Random.Int(100) < successRate(slots);
		for (Item ingredient : ingredients) {
			ingredient.quantity(ingredient.quantity() - 1);
		}
		if (success) return successOutput(slots);
		return failures.roll();
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
