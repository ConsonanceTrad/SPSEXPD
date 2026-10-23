/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pd.items.consum.food.WaterItem;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.consum.materials.EnergyFlask;
import pd.items.consum.materials.Tinder;
import pd.items.consum.materials.WillOWisp;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.AbstractConcoction;
import pd.items.consum.potions.ChaoticConcoction;
import pd.items.consum.potions.PotionForm;
import pd.items.consum.potions.exotic.ExoticPotion;
import pd.items.consum.scrolls.ScrollOfIdentify;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 表驱动的炼金药水线公式（数据源 {@code assets/data/alchemy.csv}）。
 *
 * <p>一个实例对应 CSV 里**同一 {@code id} 的所有行**（每行是一套辅材方案）。
 * 公式的主材、辅材、能量、成功率、失败分布、产物全部来自表格，便于做平衡调整。
 *
 * <p>设计文档：{@code docs/alchemy-rework.md} §7 / §8。
 */
public class TableBrewRecipe extends BrewRecipe {

	/** 公式编号（CSV 的 id，仅用于日志与排错）。 */
	public String id = "";

	/** 产物规则，见 CSV 的 rule 列。 */
	public String rule = "ITEM";

	/** rule=ITEM 时的固定产物。 */
	public Class<? extends Item> outputType;

	/** 产物数量区间（默认 1）。 */
	public int minQty = 1;
	public int maxQty = 1;

	/** 主材类型决定基准成功率时使用；为空则使用 {@code baseRate}。 */
	private Map<Class<? extends Item>, Integer> rateByMain;

	public void rateByMain(Map<Class<? extends Item>, Integer> map) {
		this.rateByMain = map;
	}

	@Override
	protected int baseRate(Item main) {
		if (rateByMain != null && main != null) {
			for (Map.Entry<Class<? extends Item>, Integer> e : rateByMain.entrySet()) {
				if (e.getKey().isInstance(main)) return e.getValue();
			}
		}
		return baseRate;
	}

	@Override
	public boolean matches(Slots ingredients) {
		if (!super.matches(ingredients)) return false;
		switch (rule) {
			case "CRUDE":
			case "REFINED":
				//主材必须能映射到某种药剂（果实/蔬菜）
				return SpsBrewRecipes.potionType(ingredients.main) != null;
			case "EXOTIC":
				return SpsBrewRecipes.exoticOf(ingredients.main) != null;
			case "TABLET":
			case "INJECTOR":
			case "REAGENT":
				return SpsBrewRecipes.isPlainPotion(ingredients.main);
			default:
				return true;
		}
	}

	@Override
	protected Item successOutput(Slots ingredients) {
		switch (rule) {
			case "CRUDE":
				return SpsBrewRecipes.variant(SpsBrewRecipes.potionType(ingredients.main), PotionForm.CRUDE);
			case "REFINED":
				return SpsBrewRecipes.variant(SpsBrewRecipes.potionType(ingredients.main), PotionForm.REFINED);
			case "EXOTIC": {
				Class<? extends ExoticPotion> exotic = SpsBrewRecipes.exoticOf(ingredients.main);
				return exotic == null ? null : Reflection.newInstance(exotic);
			}
			case "TABLET":
				return SpsBrewRecipes.variantOf(ingredients.main, PotionForm.TABLET);
			case "INJECTOR":
				return SpsBrewRecipes.variantOf(ingredients.main, PotionForm.INJECTOR);
			case "REAGENT": {
				Item reagent = SpsBrewRecipes.variantOf(ingredients.main, PotionForm.REAGENT);
				applyQty(reagent);
				return reagent;
			}
			case "BENIGN":
				extraOutputs.add(new pd.items.consum.potions.MalignantConcoction());
				return new pd.items.consum.potions.BenignConcoction();
			default: {
				if (outputType == null) return null;
				Item item = Reflection.newInstance(outputType);
				applyQty(item);
				return item;
			}
		}
	}

	private void applyQty(Item item) {
		if (item == null) return;
		if (maxQty > minQty) {
			item.quantity(Random.IntRange(minQty, maxQty));
		} else if (minQty > 1) {
			item.quantity(minQty);
		}
	}

	//*** CSV 解析

	/** 表格里可用的类型名（简单名 → 类）。除这些之外的类名走 pd.items 反射兜底。 */
	private static final Map<String, Class<? extends Item>> NAMED = new LinkedHashMap<>();
	static {
		NAMED.put("Item", Item.class);
		NAMED.put("Fruit", Fruit.class);
		NAMED.put("Vegetable", Vegetable.class);
		NAMED.put("Potion", Potion.class);
		NAMED.put("WaterItem", WaterItem.class);
		NAMED.put("Tinder", Tinder.class);
		NAMED.put("WillOWisp", WillOWisp.class);
		NAMED.put("EnergyFlask", EnergyFlask.class);
		NAMED.put("ChaoticConcoction", ChaoticConcoction.class);
		NAMED.put("AbstractConcoction", AbstractConcoction.class);
		NAMED.put("ScrollOfIdentify", ScrollOfIdentify.class);
		NAMED.put("EyeOfTruth", pd.items.consum.materials.EyeOfTruth.class);
	}

	@SuppressWarnings("unchecked")
	static Class<? extends Item> resolve(String name) {
		if (name == null || name.isEmpty()) return null;
		Class<? extends Item> known = NAMED.get(name);
		if (known != null) return known;
		for (String pkg : new String[]{"pd.items.", "pd.items.consum.potions.",
				"pd.items.consum.potions.brews.", "pd.items.consum.potions.elixirs.",
				"pd.items.consum.potions.exotic.", "pd.items.consum.materials.",
				"pd.items.consum.food.", "pd.items.consum.food.fruit.",
				"pd.items.consum.food.vegetable.", "pd.items.consum.food.processed.",
				"pd.items.consum.scrolls.", "pd.items.misc."}) {
			try {
				Class<?> c = Class.forName(pkg + name);
				if (Item.class.isAssignableFrom(c)) return (Class<? extends Item>) c;
			} catch (ClassNotFoundException ignored) {
				//继续尝试下一个包
			}
		}
		return null;
	}

	/** 解析一行的「类型#形态」写法。 */
	private static Class<? extends Item> resolveType(String text) {
		int hash = text.indexOf('#');
		String name = hash >= 0 ? text.substring(0, hash) : text;
		return resolve(name.trim());
	}

	private static java.util.function.Predicate<Item> formFilter(String text) {
		int hash = text.indexOf('#');
		if (hash < 0) return null;
		String form = text.substring(hash + 1).trim();
		switch (form) {
			case "CRUDE":
				return SpsBrewRecipes::isCrude;
			case "REFINED":
				return item -> item instanceof Potion && ((Potion) item).form == PotionForm.REFINED;
			case "TABLET":
				return item -> item instanceof Potion && ((Potion) item).form == PotionForm.TABLET;
			case "INJECTOR":
				return item -> item instanceof Potion && ((Potion) item).form == PotionForm.INJECTOR;
			case "REAGENT":
				return item -> item instanceof Potion && ((Potion) item).form == PotionForm.REAGENT;
			default:
				return null;
		}
	}

	/**
	 * 把 CSV 文本解析成配方列表。任一 id 出现解析错误则该 id 被跳过（不影响其它公式）。
	 */
	public static List<BrewRecipe> parse(String text) {
		List<BrewRecipe> result = new ArrayList<>();
		if (text == null) return result;

		String[] lines = text.split("\\r?\\n");
		List<String[]> rows = new ArrayList<>();
		for (String raw : lines) {
			if (raw == null) continue;
			String line = raw.trim();
			if (line.isEmpty() || line.startsWith("#")) continue;
			if (line.toLowerCase().startsWith("id,plan,")) continue; //表头
			String[] cells = line.split(",", -1);
			if (cells.length < 21) continue;
			rows.add(cells);
		}

		//按 id 分组（保持出现顺序），每组一个 TableBrewRecipe
		Map<String, TableBrewRecipe> byId = new LinkedHashMap<>();
		Map<String, List<AdjuvantSet>> plansById = new LinkedHashMap<>();
		Map<String, List<String[]>> rowsById = new LinkedHashMap<>();
		for (String[] cells : rows) {
			String id = cells[0].trim();
			rowsById.computeIfAbsent(id, k -> new ArrayList<>()).add(cells);
		}

		for (Map.Entry<String, List<String[]>> entry : rowsById.entrySet()) {
			try {
				List<String[]> group = entry.getValue();
				String[] first = group.get(0);

				List<AdjuvantSet> plans = new ArrayList<>();
				boolean badPlan = false;
				for (String[] cells : group) {
					boolean needsAdj = !cells[3].trim().isEmpty()
							|| !cells[4].trim().isEmpty()
							|| !cells[5].trim().isEmpty();
					AdjuvantSet set = buildAdjuvantSet(cells);
					if (needsAdj && set == null) {
						//辅材类型名无法解析：整条公式跳过，绝不退化成「不需要辅材」
						badPlan = true;
						break;
					}
					if (set != null) plans.add(set);
				}
				if (badPlan) continue;

				TableBrewRecipe recipe = new TableBrewRecipe();
				recipe.id = entry.getKey();
				recipe.mainMaterial = resolveType(first[2].trim());
				recipe.adjuvantOptions = plans.isEmpty() ? null : plans.toArray(new AdjuvantSet[0]);
				recipe.energyCost = parseInt(first[10], 0);
				recipe.needsWater = parseInt(first[12], 0) != 0;
				recipe.needsFire = parseInt(first[13], 0) != 0;

				//成功率：单一值，或「40:Fruit|60:Vegetable」
				String rateText = first[11].trim();
				if (rateText.contains(":")) {
					Map<Class<? extends Item>, Integer> map = new LinkedHashMap<>();
					int firstValue = 0;
					for (String part : rateText.split("\\|")) {
						//写法是「值:类型」，如 40:Fruit
						String[] kv = part.split(":");
						int value = parseInt(kv[0], 0);
						Class<? extends Item> type = resolve(kv.length > 1 ? kv[1].trim() : "");
						if (type != null) map.put(type, value);
						if (firstValue == 0) firstValue = value;
					}
					recipe.rateByMain(map);
					recipe.baseRate = firstValue;
				} else {
					recipe.baseRate = parseInt(rateText, 100);
				}

				//产物
				recipe.rule = first[7].trim().isEmpty() ? "ITEM" : first[7].trim();
				String outputText = first[8].trim();
				if (!outputText.isEmpty()) recipe.outputType = resolveType(outputText);
				String qtyText = first[9].trim();
				if (qtyText.contains("-")) {
					String[] q = qtyText.split("-");
					recipe.minQty = parseInt(q[0], 1);
					recipe.maxQty = parseInt(q.length > 1 ? q[1] : q[0], recipe.minQty);
				} else if (!qtyText.isEmpty()) {
					recipe.minQty = recipe.maxQty = parseInt(qtyText, 1);
				}

				//失败分布
				recipe.failures = buildFailures(first);

				//rule=ITEM 必须有产物类与主材；其余规则允许 main 留空（由产物规则自判主材类型）
				if ("ITEM".equals(recipe.rule)
						&& (recipe.outputType == null || recipe.mainMaterial == null)) continue;

				result.add(recipe);
			} catch (RuntimeException e) {
				//单条公式解析失败不影响其它公式（并由上层回退到内置表）
			}
		}

		return result;
	}

	private static AdjuvantSet buildAdjuvantSet(String[] cells) {
		List<AdjuvantSet.Slot> slots = new ArrayList<>();
		int[] bonuses = parseBonuses(cells[6]);
		int bonusIndex = 0;
		for (int i = 3; i <= 5; i++) {
			String text = cells[i].trim();
			if (text.isEmpty()) continue;
			Class<? extends Item> type = resolveType(text);
			if (type == null) return null;
			int bonus = bonusIndex < bonuses.length ? bonuses[bonusIndex] : 0;
			bonusIndex++;
			slots.add(new AdjuvantSet.Slot(type, bonus, formFilter(text)));
		}
		if (slots.isEmpty()) return null;
		return new AdjuvantSet(slots.toArray(new AdjuvantSet.Slot[0]));
	}

	private static int[] parseBonuses(String text) {
		if (text == null || text.trim().isEmpty()) return new int[0];
		String[] parts = text.trim().split("/");
		int[] result = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			result[i] = parseInt(parts[i], 0);
		}
		return result;
	}

	private static FailureTable buildFailures(String[] cells) {
		List<Integer> weights = new ArrayList<>();
		List<java.util.function.Supplier<Item>> outputs = new ArrayList<>();
		int[][] pairs = {{14, 15}, {16, 17}, {18, 19}};
		for (int[] pair : pairs) {
			String name = cells[pair[0]].trim();
			if (name.isEmpty()) continue;
			java.util.function.Supplier<Item> maker = failureMaker(name);
			if (maker == null) continue;
			outputs.add(maker);
			weights.add(parseInt(cells[pair[1]], 0));
		}
		if (outputs.isEmpty()) return null;
		int[] w = new int[weights.size()];
		for (int i = 0; i < w.length; i++) w[i] = weights.get(i);
		return new FailureTable(w, outputs.toArray(new java.util.function.Supplier[0]));
	}

	@SuppressWarnings("unchecked")
	private static java.util.function.Supplier<Item> failureMaker(String name) {
		if ("*exotic".equals(name)) return SpsBrewRecipes::randomExotic;
		if ("Waste".equals(name)) return pd.items.consum.materials.Waste::new;
		if ("ChaoticConcoction".equals(name)) return ChaoticConcoction::new;
		Class<? extends Item> type = resolveType(name);
		if (type == null) return null;
		return () -> Reflection.newInstance(type);
	}

	private static int parseInt(String text, int fallback) {
		if (text == null) return fallback;
		String trimmed = text.trim();
		if (trimmed.isEmpty()) return fallback;
		try {
			return Integer.parseInt(trimmed);
		} catch (NumberFormatException e) {
			return fallback;
		}
	}
}
