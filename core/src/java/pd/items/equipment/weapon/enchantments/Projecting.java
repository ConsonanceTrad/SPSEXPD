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
import pd.messages.InlineText;

public class Projecting extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Projecting.class)
			.t("name", "索敌%s")
			.t("desc", "这个附魔会使近战武器获得额外的攻击距离。远程武器则能够在瞄准附近目标时穿透墙壁。")
			.t("elestrike_desc", "武器拥有索敌附魔时，元素打击对范围内除主要目标外的每个敌人都造成30%的伤害。");
	}




	private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing( 0x8844CC );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		//Does nothing as a proc, instead increases weapon range.
		//See weapon.reachFactor, and MissileWeapon.throwPos;
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return PURPLE;
	}

}
