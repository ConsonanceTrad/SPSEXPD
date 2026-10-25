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

package pd.utils;

import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.potions.Potion;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.stones.Runestone;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.plants.Plant;

/**
 * SPSEXPD: 嬗变效果（原「嬗变卷轴」的核心逻辑）。
 *
 * <p>卷轴下架后，效果以这两个入口继续存在：嬗变之球、艺人技能等。
 * 这里保留原实现的语义——把一件物品换成同类的另一件；如果拿不到同类物品就原样返回。
 */
public final class Transmutation {

	private Transmutation() {}

	public static Item changeItem(Item item) {
		if (item == null) return null;

		if (item instanceof Scroll) {
			return Generator.randomUsingDefaults(Generator.Category.SCROLL);
		} else if (item instanceof Potion) {
			return Generator.randomUsingDefaults(Generator.Category.POTION);
		} else if (item instanceof Runestone) {
			return Generator.randomUsingDefaults(Generator.Category.STONE);
		} else if (item instanceof Plant.Seed) {
			return Generator.randomUsingDefaults(Generator.Category.SEED);
		} else if (item instanceof Wand) {
			return Generator.randomUsingDefaults(Generator.Category.WAND);
		} else if (item instanceof Ring) {
			return Generator.randomUsingDefaults(Generator.Category.RING);
		} else if (item instanceof MissileWeapon) {
			return Generator.randomUsingDefaults(Generator.Category.MISSILE);
		} else if (item instanceof Armor) {
			return Generator.randomUsingDefaults(Generator.Category.ARMOR);
		} else if (item instanceof Weapon) {
			return Generator.randomUsingDefaults(Generator.Category.WEAPON);
		}
		return item;
	}
}
