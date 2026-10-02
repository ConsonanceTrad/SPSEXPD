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
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.levels.traps.GeyserTrap;
import pd.mechanics.Ballistica;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Pressurized extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Pressurized.class)
			.t("name", "激流%s")
			.t("desc", "激流诅咒的武器可以喷流出强劲的水爆，击退包括你在内的一切事物。")
			.t("elestrike_desc", "武器拥有激流诅咒时，元素打击会创造破坏地形并击退范围内所有敌人的巨浪。");
	}




	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		float procChance = 1/8f * procChanceMultiplier(attacker);

		if (Random.Float() < procChance){

			GeyserTrap geyser = new GeyserTrap();

			geyser.pos = defender.pos;
			if (weapon instanceof MeleeWeapon){
				Ballistica aim = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
				if (aim.path.size() > aim.dist+1) {
					geyser.centerKnockBackDirection = aim.path.get(aim.dist + 1);
				}
			}
			geyser.source = this;

			geyser.activate();

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
