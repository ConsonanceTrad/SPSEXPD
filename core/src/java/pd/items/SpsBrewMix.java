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

import pd.Statistics;
import pd.items.consum.food.processed.*;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionForm;
import pd.items.consum.potions.brews.*;
import pd.plants.PlantHarvest;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 合剂 / 魔药的「本局随机两味辅材」（docs/alchemy-rework.md §7.3）。
 *
 * <p>开局从池子里随机抽两味并写进存档，本局固定。玩家**不被告知**这两味是什么（后续会加
 * 揭示机制），因此 {@link #recipeA()} / {@link #recipeB()} 是留给未来 UI 的读取入口。
 *
 * <p>匹配规则：4 个辅材格里只要**同时出现**这两味即算命中，不要求数量精确。
 */
public final class SpsBrewMix {

	private SpsBrewMix() {}

	//*** 池子

	/** 蔬菜二次加工产物（由蔬菜加工而来，是合剂/魔药的辅材池成员）。 */
	private static final Class<? extends Item>[] PROCESSED = new Class[]{
			Adhesive.class, Capsaicin.class, CoolingOil.class, HealingSalve.class,
			NutrientSolution.class, FruitThread.class, RedRose.class, SunflowerSeed.class,
			Sedative.class, Perfume.class, WakeTea.class, TransmutePowder.class,
			ToxicExtract.class, DigestiveFluid.class, CrystalShard.class, HighEnergySpore.class,
			HormoneSolution.class, AetherLiquid.class, WishPetal.class,
			Salt.class, Syrup.class, PickledMeat.class, Mince.class
	};

	/** 池条目：类型 + 可选形态过滤（粗制药水是一类，不是单个类）。 */
	public static final class PoolEntry {
		public final Class<? extends Item> type;
		public final java.util.function.Predicate<Item> filter;

		PoolEntry(Class<? extends Item> type, java.util.function.Predicate<Item> filter) {
			this.type = type;
			this.filter = filter;
		}

		public boolean accepts(Item item) {
			return type.isInstance(item) && (filter == null || filter.test(item));
		}
	}

	private static List<PoolEntry> pool;

	public static List<PoolEntry> pool() {
		if (pool == null) {
			List<PoolEntry> list = new ArrayList<>();
			//粗制药水（任意药剂的粗制形态）
			list.add(new PoolEntry(Potion.class, SpsBrewRecipes::isCrude));
			//魔法蔬菜 = 植物踩踏/种植产出的蔬菜
			for (Class<? extends Item> vegetable : PlantHarvest.vegetables()) {
				list.add(new PoolEntry(vegetable, null));
			}
			//蔬菜二次加工产物
			for (Class<? extends Item> processed : PROCESSED) {
				list.add(new PoolEntry(processed, null));
			}
			pool = list;
		}
		return pool;
	}

	//*** 本局配方

	/**
	 * 掷一次本局的两味配方（开局调用；旧档缺字段时也由 {@link #ensureRolled()} 补掷）。
	 */
	public static void roll() {
		List<PoolEntry> entries = pool();
		if (entries.size() < 2) {
			Statistics.spsMixRecipeA = "";
			Statistics.spsMixRecipeB = "";
			return;
		}
		int a = Random.Int(entries.size());
		int b = Random.Int(entries.size() - 1);
		if (b >= a) b++;
		Statistics.spsMixRecipeA = label(entries.get(a));
		Statistics.spsMixRecipeB = label(entries.get(b));
	}

	/** 确保本局配方已存在（新开局已掷；读旧档时补一次）。 */
	public static void ensureRolled() {
		if (Statistics.spsMixRecipeA == null || Statistics.spsMixRecipeA.isEmpty()
				|| Statistics.spsMixRecipeB == null || Statistics.spsMixRecipeB.isEmpty()) {
			roll();
		}
	}

	/** 池条目在存档里的稳定标识（类型全名 + 形态标记）。 */
	private static String label(PoolEntry entry) {
		String form = entry.filter == null ? "" : "#CRUDE";
		return entry.type.getName() + form;
	}

	private static PoolEntry byLabel(String label) {
		if (label == null || label.isEmpty()) return null;
		for (PoolEntry entry : pool()) {
			if (label(entry).equals(label)) return entry;
		}
		return null;
	}

	/** 本局第一味（可能为 null，表示池子异常）。 */
	public static PoolEntry recipeA() {
		ensureRolled();
		return byLabel(Statistics.spsMixRecipeA);
	}

	/** 本局第二味。 */
	public static PoolEntry recipeB() {
		ensureRolled();
		return byLabel(Statistics.spsMixRecipeB);
	}

	/** 4 个辅材格里是否同时包含本局那两味。 */
	public static boolean containsRecipe(BrewRecipe.Slots slots) {
		PoolEntry a = recipeA();
		PoolEntry b = recipeB();
		if (a == null || b == null) return false;
		boolean hasA = false;
		boolean hasB = false;
		for (Item item : slots.adjuvant) {
			if (item == null) continue;
			if (a.accepts(item)) hasA = true;
			if (b.accepts(item)) hasB = true;
		}
		return hasA && hasB;
	}

	//*** 魔药映射（标准药水 → 魔药，13 种 1:1）

	private static Map<Class<? extends Potion>, Class<? extends Brew>> elixirs;

	public static Class<? extends Brew> elixirFor(Item standardPotion) {
		if (!(standardPotion instanceof Potion)) return null;
		if (elixirs == null) {
			Map<Class<? extends Potion>, Class<? extends Brew>> map = new LinkedHashMap<>();
			map.put(pd.items.consum.potions.PotionOfLiquidFlame.class, InfernalBrew.class);
			map.put(pd.items.consum.potions.PotionOfFrost.class, BlizzardBrew.class);
			map.put(pd.items.consum.potions.PotionOfToxicGas.class, CausticBrew.class);
			map.put(pd.items.consum.potions.PotionOfLevitation.class, ShockingBrew.class);
			map.put(pd.items.consum.potions.PotionOfPurity.class, AquaBrew.class);
			map.put(pd.items.consum.potions.PotionOfParalyticGas.class, UnstableBrew.class);
			//以下 7 种暂无专属效果，用 +10 护盾占位
			map.put(pd.items.consum.potions.PotionOfStrength.class, SpsPlaceholderElixirs.TitanBrew.class);
			map.put(pd.items.consum.potions.PotionOfHealing.class, SpsPlaceholderElixirs.VitalBrew.class);
			map.put(pd.items.consum.potions.PotionOfMindVision.class, SpsPlaceholderElixirs.InsightBrew.class);
			map.put(pd.items.consum.potions.PotionOfAcid.class, SpsPlaceholderElixirs.CorrodeBrew.class);
			map.put(pd.items.consum.potions.PotionOfHaste.class, SpsPlaceholderElixirs.SwiftBrew.class);
			map.put(pd.items.consum.potions.PotionOfInvisibility.class, SpsPlaceholderElixirs.VeilBrew.class);
			map.put(pd.items.consum.potions.PotionOfExperience.class, SpsPlaceholderElixirs.AscendBrew.class);
			elixirs = map;
		}
		return elixirs.get(standardPotion.getClass());
	}

	/** 生成一瓶该标准药水对应的魔药（无对应则 null）。 */
	public static Item brewElixir(Item standardPotion) {
		Class<? extends Brew> type = elixirFor(standardPotion);
		return type == null ? null : Reflection.newInstance(type);
	}

	/** 是否为「标准药水」形态。 */
	public static boolean isStandard(Item item) {
		return item instanceof Potion && ((Potion) item).form == PotionForm.STANDARD;
	}

	/** 是否为「粗制药水」形态。 */
	public static boolean isCrude(Item item) {
		return SpsBrewRecipes.isCrude(item);
	}

	/** 是否为蔬菜（魔法蔬菜）。 */
	public static boolean isVegetable(Item item) {
		return item instanceof Vegetable;
	}
}
