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
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionForm;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 商店「药水位」的商品池（docs/alchemy-rework.md §7）。
 *
 * <p>药水位不再只出普通药水：粗制药水、标准药水、药片、缓释片、安培、魔法果汁、魔法硬糖
 * 与普通药水**共同**占用商店的药水位。
 *
 * <p>药水类商品按炼金口径保持**未鉴定**（玩家看到的是「未鉴定药剂」，形态与真实类型要喝过/鉴定过才知道）；
 * 魔法果汁与魔法硬糖是食物，直接可见。
 */
public final class SpsShopPotion {

	private SpsShopPotion() {}

	public static Item random() {
		int roll = Random.Int(100);
		//普通药水仍是最常见的一档
		if (roll < 50) return Generator.random(Generator.Category.POTION);

		PotionForm form;
		if (roll < 62) {
			form = PotionForm.CRUDE;
		} else if (roll < 70) {
			form = PotionForm.STANDARD;
		} else if (roll < 78) {
			form = PotionForm.PILL;
		} else if (roll < 89) {
			form = PotionForm.TABLET;
		} else if (roll < 94) {
			form = PotionForm.AMPOULE;
		} else {
			//魔法果汁 / 魔法硬糖
			Item food = roll < 97 ? new MagicJuice() : new MagicCandy();
			return food;
		}

		Class<?>[] pool = Generator.Category.POTION.classes;
		if (pool == null || pool.length == 0) return Generator.random(Generator.Category.POTION);
		Class<?> type = pool[Random.Int(pool.length)];
		Item item = Reflection.newInstance((Class<? extends Item>) type);
		if (!(item instanceof Potion)) return Generator.random(Generator.Category.POTION);

		Potion potion = SpsBrewRecipes.variantAny(item, form);
		if (potion == null) return Generator.random(Generator.Category.POTION);
		//SPSEXPD: 商店卖的药剂一律未鉴定，形态不提前暴露
		potion.unidentify();
		return potion;
	}
}
