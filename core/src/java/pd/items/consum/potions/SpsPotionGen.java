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

package pd.items.consum.potions;

import render.utils.math.Random;

/**
 * SPSEXPD: 自然刷新 / 商店售卖时，普通药剂的**形态**抽取（docs/alchemy-rework.md §4）。
 *
 * <p>原版 NORMAL 形态**不再出现在任何自然生成里**：拾到的药剂一定是粗制 / 标准 /
 * 药片 / 缓释片 / 安培之一，并且处于未鉴定状态；只有炼金产出（炼金釜、蒸馏压片机、
 * 分装灌液机）才会直接给出已鉴定的成品。
 */
public final class SpsPotionGen {

	private SpsPotionGen() {}

	/** 各形态权重（合计 100）。 */
	private static final int W_CRUDE = 60;
	private static final int W_STANDARD = 15;
	private static final int W_PILL = 10;
	private static final int W_TABLET = 10;
	private static final int W_AMPOULE = 5;

	/** 随机取一个「自然刷新」形态（永不返回 {@link PotionForm#NORMAL}）。 */
	public static PotionForm randomNaturalForm() {
		int roll = Random.Int(W_CRUDE + W_STANDARD + W_PILL + W_TABLET + W_AMPOULE);
		if (roll < W_CRUDE) return PotionForm.CRUDE;
		roll -= W_CRUDE;
		if (roll < W_STANDARD) return PotionForm.STANDARD;
		roll -= W_STANDARD;
		if (roll < W_PILL) return PotionForm.PILL;
		roll -= W_PILL;
		if (roll < W_TABLET) return PotionForm.TABLET;
		return PotionForm.AMPOULE;
	}
}
