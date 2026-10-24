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
import pd.items.misc.LuckyBadge;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.messages.InlineText;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 「魔法植物」——地图上少量高草被替换成的植株（docs/alchemy-rework.md §4）。
 *
 * <p>**不是**把植物分成三档，而是同一株植物带有三种**形态**之一，形态只影响踩踏产出：
 * <ul>
 *   <li>{@link #WILD} 野生：散 1 枚果实（+幸运加成），不给蔬菜</li>
 *   <li>{@link #CULTIVATED} 人工：散 1~2 枚果实，蔬菜 1</li>
 *   <li>{@link #CAREFUL} 精心：散 2~4 枚果实（含大型果实概率），蔬菜 2</li>
 * </ul>
 * 植株沿用原植物的贴图与名称。
 */
public class MagicPlant extends Plant {

	static {
		InlineText.of(MagicPlant.class)
			.t("name", "魔法植物")
			.t("desc", "一株被魔法浸润的作物，看起来比同类更繁茂。踩踏它就能收获果实与蔬菜。");
	}

	/** 形态：野生。 */
	public static final int WILD = 0;
	/** 形态：人工。 */
	public static final int CULTIVATED = 1;
	/** 形态：精心。 */
	public static final int CAREFUL = 2;

	/** 本株的形态（只影响踩踏产出）。 */
	public int form = WILD;

	/** 原植物类（贴图/名称/产出映射来源）。 */
	public Class<? extends Plant> base;

	public MagicPlant() {
	}

	public MagicPlant(Class<? extends Plant> base, int form) {
		this.base = base;
		this.form = form;
		Plant prototype = Reflection.newInstance(base);
		if (prototype != null) {
			image = prototype.image;
			seedClass = prototype.seedClass;
		}
	}

	//*** 生成

	/** 每层最多出现的株数。 */
	private static final int MAX_PER_LEVEL = 3;
	/** 每格高草被替换的概率。 */
	private static final float CHANCE = 0.06f;

	/**
	 * 在当前楼层把少量高草替换为魔法植物（草地绘制完成后调用）。
	 */
	public static void sprinkle(Level level) {
		if (level == null) return;
		List<Class<? extends Plant>> pool = PlantHarvest.plants();
		if (pool.isEmpty()) return;

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

			//形态权重：野生 40% / 人工 40% / 精心 20%
			int roll = Random.Int(100);
			int form = roll < 40 ? WILD : roll < 80 ? CULTIVATED : CAREFUL;
			MagicPlant plant = new MagicPlant(pool.get(Random.Int(pool.size())), form);
			plant.pos = cell;
			level.plants.put(cell, plant);
			placed++;
		}
	}

	//*** 踩踏产出

	@Override
	public void activate(Char ch) {
		//产出统一在 trigger 里结算
	}

	@Override
	public void trigger(Char ch, boolean interruptHero, boolean accrual) {
		if (ch instanceof Hero && interruptHero) {
			((Hero) ch).interrupt();
		}
		if (Dungeon.level == null) {
			wither();
			return;
		}

		Class<? extends Plant> source = base == null ? getClass() : base;
		PlantHarvest.Species species = PlantHarvest.speciesFor(source);
		Hero hero = ch instanceof Hero ? (Hero) ch : null;

		if (species != null) {
			//果实散到相邻格（邻格不足时溢到踩踏格）
			int fruitCount;
			int vegetableCount;
			switch (form) {
				case CAREFUL:
					fruitCount = Random.NormalIntRange(2, 4);
					vegetableCount = 2;
					break;
				case CULTIVATED:
					fruitCount = Random.NormalIntRange(1, 2);
					vegetableCount = 1;
					break;
				default:
					fruitCount = 1;
					vegetableCount = 0;
					break;
			}
			fruitCount += LuckyBadge.plantExtraFruit(hero);
			float largeChance = LuckyBadge.largeFruitChance(hero);

			ArrayList<Integer> candidates = PlantHarvest.neighbours(Dungeon.level, pos);
			for (int i = 0; i < fruitCount; i++) {
				int cell = candidates.isEmpty() ? pos : candidates.remove(Random.Int(candidates.size()));
				Class<? extends Item> type = species.fruit;
				if (type == null) break;
				if (species.largeFruit != null && Random.Float() < largeChance) type = species.largeFruit;
				PlantHarvest.drop(Dungeon.level, cell, type, pos);
			}
			for (int i = 0; i < vegetableCount; i++) {
				if (species.vegetable == null) break;
				PlantHarvest.drop(Dungeon.level, pos, species.vegetable, pos);
			}
		}

		wither();
	}

	//*** 存档

	private static final String FORM = "magic_form";
	private static final String BASE = "magic_base";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(FORM, form);
		bundle.put(BASE, base == null ? "" : base.getName());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		form = bundle.contains(FORM) ? bundle.getInt(FORM) : WILD;
		String name = bundle.contains(BASE) ? bundle.getString(BASE) : "";
		if (!name.isEmpty()) {
			try {
				Class<?> c = Class.forName(name);
				if (Plant.class.isAssignableFrom(c)) {
					base = (Class<? extends Plant>) c;
				}
			} catch (ClassNotFoundException ignored) {
				//基类丢失时仍可按形态产出（只是没有果实/蔬菜映射）
			}
		}
	}
}
