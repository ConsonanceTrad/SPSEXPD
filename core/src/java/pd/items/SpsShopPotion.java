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

import pd.items.consum.food.MagicCandy;
import pd.items.consum.food.MagicJuice;
import render.utils.math.Random;

/**
 * SPSEXPD: 商店「药水位」的商品池（docs/alchemy-rework.md §7）。
 *
 * <p>药水位现在从 `Generator.random(Category.POTION)` 取货——它已统一处理成
 * 「新炼金形态（粗制/标准/药片/缓释片/安培）+ 未鉴定」；另外小概率上架
 * 魔法果汁 / 魔法硬糖（食物，不走药剂池）。
 */
public final class SpsShopPotion {

	private SpsShopPotion() {}

	public static Item random() {
		//小概率出现果汁/硬糖，其余都是「未鉴定的新形态药剂」
		int roll = Random.Int(100);
		if (roll < 95) return Generator.random(Generator.Category.POTION);
		return roll < 98 ? new MagicJuice() : new MagicCandy();
	}
}
