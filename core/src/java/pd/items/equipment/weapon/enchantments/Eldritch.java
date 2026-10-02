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

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.effects.Flare;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Eldritch extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Eldritch.class)
			.t("name", "异质%s")
			.t("desc", "异质附魔的武器会使目睹其攻击的附近敌人心生恐惧，使其狂乱逃离攻击者。")
			.t("elestrike_desc", "武器拥有异质附魔时，元素打击会为范围内包括主目标的所有敌人施加更为持久的恐惧效果。");
	}




	private static ItemSprite.Glowing GREY = new ItemSprite.Glowing( 0x222222 );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = Math.max( 0, weapon.buffedLvl() );

		// lvl 0 - 20%
		// lvl 1 - 33%
		// lvl 2 - 43%
		float procChance = (level+1f)/(level+5f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);

			for (Char ch : Actor.chars()){
				if (ch == attacker || ch == defender || ch.alignment == attacker.alignment){
					continue;
				}
				if (ch.fieldOfView != null && (ch.fieldOfView[attacker.pos] || ch.fieldOfView[defender.pos])){
					if (ch == Dungeon.hero){
						Buff.affect( defender, Vertigo.class, 5f );
					} else {
						Buff.affect(ch, Terror.class, powerMulti * 5f).object = attacker.id();
					}
				}
			}

			new Flare( 5, 24 ).color( 0xFF0000, true ).show( attacker.sprite, 1f );
		}

		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return GREY;
	}
}
