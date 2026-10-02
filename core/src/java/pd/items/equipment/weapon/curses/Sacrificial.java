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

package pd.items.equipment.weapon.curses;

import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Sacrificial extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sacrificial.class)
			.t("name", "血祭%s")
			.t("desc", "血祭诅咒会向使用者索求血液作为攻击的回报，你的状态越健康，诅咒索取的也就越多。")
			.t("elestrike_desc", "武器拥有血祭诅咒时，元素打击对英雄自身与范围内的每一个敌人都造成初始值为12点的流血效果。");
	}




	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {

		float procChance = 1/10f * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {
			float missingPercent = attacker.HP/(float)attacker.HT;
			float bleedAmt = (float)(Math.pow(missingPercent, 2) * attacker.HT)/8f;
			if (Random.Float() < bleedAmt) {
				Buff.affect(attacker, Bleeding.class).set(Math.max(1, bleedAmt), getClass());
			}
		}

		return damage;
	}

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

}
