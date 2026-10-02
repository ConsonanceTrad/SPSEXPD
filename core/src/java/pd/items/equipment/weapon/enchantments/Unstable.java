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

package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import render.utils.serialize.Reflection;
import pd.messages.InlineText;

public class Unstable extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Unstable.class)
			.t("name", "紊乱%s")
			.t("desc", "紊乱附魔的武器散发着混乱能量，每一击都视作具有另一种随机附魔。")
			.t("elestrike_desc", "武器拥有紊乱附魔时，元素打击会对范围内除主要目标以外的其他敌人都造成一种随机附魔效果。");
	}


	private static ItemSprite.Glowing GREY = new ItemSprite.Glowing( 0x999999 );

	private static Class<?extends Weapon.Enchantment>[] randomEnchants = new Class[]{
			Blazing.class,
			Blocking.class,
			Blooming.class,
			Chilling.class,
			Kinetic.class,
			Corrupting.class,
			Elastic.class,
			Grim.class,
			Lucky.class,
			//projecting not included, no on-hit effect
			Shocking.class,
			Vampiric.class,
			Venomous.class,
			Vorpal.class,
			Eldritch.class
	};

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		
		int conservedDamage = 0;
		if (attacker.buff(Kinetic.ConservedDamage.class) != null) {
			conservedDamage = attacker.buff(Kinetic.ConservedDamage.class).damageBonus();
			attacker.buff(Kinetic.ConservedDamage.class).detach();
		}
		
		damage = Reflection.newInstance(Random.oneOf(randomEnchants)).proc( weapon, attacker, defender, damage );
		
		return damage + conservedDamage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return GREY;
	}
}
