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

package pd.plants;

import java.util.ArrayList;
import java.util.List;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.messages.InlineText;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 「魔法植物」——按三档阶位出现在地图上（docs/alchemy-rework.md §4）。
 *
 * <p>普通地图的少量高草会被替换成这类植株。它本身沿用原植物的贴图与名称，
 * 区别只在踩踏产出：按阶位给「人工种植 / 精心照料」级别的蔬菜（外加 1 枚对应果实）。
 */
public class MagicPlant extends Plant {

	static {
		InlineText.of(MagicPlant.class)
			.t("name", "魔法植物")
			.t("desc", "一株被魔法浸润的作物。踩踏它能收到人工种植级别的蔬菜。");
	}

	/** 阶位：1 = 低阶（人工种植产出），2 = 中阶，3 = 高阶（精心照料产出）。 */
	public int tier = 1;

	/** 原植物类（贴图/名称来源）。 */
	public Class<? extends Plant> base;

	public MagicPlant() {
	}

	public MagicPlant(Class<? extends Plant> base, int tier) {
		this.base = base;
		this.tier = tier;
		Plant prototype = Reflection.newInstance(base);
		if (prototype != null) {
			image = prototype.image;
			seedClass = prototype.seedClass;
		}
	}

	//*** 阶位植物池

	private static final Class<? extends Plant>[] LOW = new Class[]{
			Freshberry.class, Rotberry.class, BlandfruitBush.class, Swiftthistle.class,
			Fadeleaf.class, Earthroot.class
	};

	private static final Class<? extends Plant>[] MID = new Class[]{
			Sungrass.class, Icecap.class, Firebloom.class, Stormvine.class, Sorrowmoss.class,
			Dreamfoil.class, Blindweed.class, Seedpod.class, Dewcatcher.class
	};

	private static final Class<? extends Plant>[] HIGH = new Class[]{
			Starflower.class, NutPlant.class, StarEater.class, ReNepenth.class, SiOtwoFlower.class
	};

	/** 每层最多出现的株数。 */
	private static final int MAX_PER_LEVEL = 3;
	/** 每格高草被替换的概率。 */
	private static final float CHANCE = 0.06f;

	/**
	 * 在当前楼层把少量高草替换为魔法植物（在草地绘制完成后调用）。
	 */
	public static void sprinkle(Level level) {
		if (level == null) return;
		List<Integer> candidates = new ArrayList<>();
		for (int i = 0; i < level.length(); i++) {
			if (level.map[i] == Terrain.HIGH_GRASS && level.plants.get(i) == null) {
				candidates.add(i);
			}
		}
		if (candidates.isEmpty()) return;

		int placed = 0;
		while (placed < MAX_PER_LEVEL && !candidates.isEmpty()) {
			int idx = Random.Int(candidates.size());
			int cell = candidates.remove(idx);
			if (Random.Float() >= CHANCE) continue;

			int roll = Random.Int(100);
			int tier = roll < 60 ? 1 : roll < 90 ? 2 : 3;
			MagicPlant plant = ofTier(tier);
			if (plant == null) continue;
			plant.pos = cell;
			level.plants.put(cell, plant);
			placed++;
		}
	}

	private static MagicPlant ofTier(int tier) {
		Class<? extends Plant>[] pool = tier <= 1 ? LOW : tier == 2 ? MID : HIGH;
		if (pool.length == 0) return null;
		return new MagicPlant(pool[Random.Int(pool.length)], tier);
	}

	//*** 踩踏产出

	@Override
	public void activate(Char ch) {
		//产出统一在 trigger 里结算，这里不做额外的植物效果
	}

	@Override
	public void trigger(Char ch, boolean interruptHero, boolean accrual) {
		if (ch instanceof Hero && interruptHero) {
			((Hero) ch).interrupt();
		}

		Class<? extends Item> vegetable = PlantHarvest.vegetableFor(getClass());
		if (vegetable == null && base != null) {
			vegetable = PlantHarvest.vegetableFor(base);
		}

		int vegetables = tier <= 1 ? 1 : tier == 2 ? Random.IntRange(1, 2) : Random.IntRange(2, 3);
		if (vegetable != null) {
			for (int i = 0; i < vegetables; i++) {
				PlantHarvest.drop(Dungeon.level, pos, vegetable, pos);
			}
		}

		if (tier >= 3) {
			Class<? extends Item> fruit = PlantHarvest.fruitFor(base == null ? getClass() : base);
			if (fruit != null) {
				PlantHarvest.drop(Dungeon.level, pos, fruit, pos);
			}
		}

		wither();
	}

	//*** 存档

	private static final String TIER = "magic_tier";
	private static final String BASE = "magic_base";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TIER, tier);
		bundle.put(BASE, base == null ? "" : base.getName());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		tier = bundle.contains(TIER) ? bundle.getInt(TIER) : 1;
		String name = bundle.contains(BASE) ? bundle.getString(BASE) : "";
		if (!name.isEmpty()) {
			try {
				Class<?> c = Class.forName(name);
				if (Plant.class.isAssignableFrom(c)) {
					base = (Class<? extends Plant>) c;
				}
			} catch (ClassNotFoundException ignored) {
				//基类丢失时退化为纯魔法植物（仍可按阶位产出）
			}
		}
	}
}
