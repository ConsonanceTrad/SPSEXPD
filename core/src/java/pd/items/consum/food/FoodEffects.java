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

package pd.items.consum.food;

import java.lang.reflect.Method;

import pd.actors.hero.Hero;
import pd.items.Item;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 蔬菜的食用效果复用——果汁与硬糖都「效果跟蔬菜走」。
 *
 * <p>蔬菜的 {@code onEat(Hero)} 是 protected（且各子类自己覆写），这里用反射调用，
 * 免得为了调用一句就改所有蔬菜的可见性。
 */
final class FoodEffects {

	private FoodEffects() {}

	static void applyVegetableEffect(Class<? extends Item> vegetable, Hero hero) {
		if (vegetable == null || hero == null) return;
		Item veg = Reflection.newInstance(vegetable);
		if (veg == null) return;
		Method method = findOnEat(veg.getClass());
		if (method == null) return;
		try {
			method.setAccessible(true);
			method.invoke(veg, hero);
		} catch (Exception ignored) {
			//效果失败不影响吃东西本身
		}
	}

	private static Method findOnEat(Class<?> type) {
		Class<?> current = type;
		while (current != null && current != Object.class) {
			try {
				return current.getDeclaredMethod("onEat", Hero.class);
			} catch (NoSuchMethodException ignored) {
				current = current.getSuperclass();
			}
		}
		return null;
	}
}
