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

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Vorpal extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Vorpal.class)
			.t("name", "锋锐%s")
			.t("desc", "锋锐附魔的武器极其致命，其干净利落的攻击有概率对可流血的敌人施加流血效果而非造成直接伤害。")
			.t("elestrike_desc", "武器拥有斩杀附魔时，元素打击会对范围内出主目标外的所有敌人施加额外的流血效果。");
	}




	private static ItemSprite.Glowing RED = new ItemSprite.Glowing( 0xAA6666 );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		if (defender.isImmune(Bleeding.class)){
			return damage;
		}

		//flat 25% proc chance, effect scales with damage dealt
		float procChance = 1/4f * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);

			//we use a buff to track so we can know the final dmg
			Buff.affect(attacker, VorpalTracker.class).powerMulti = powerMulti;
		}

		return damage;
	}

	public static class VorpalTracker extends Buff {
		{
			actPriority = Actor.VFX_PRIO;
		}

		public float powerMulti;

		@Override
		public boolean act() {
			detach();
			return true;
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return RED;
	}
}
