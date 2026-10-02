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

package pd.items.equipment.armor.curses;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.items.equipment.armor.Armor.Glyph;
import pd.items.equipment.armor.Armor;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite.Glowing;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Metabolism extends Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Metabolism.class)
			.t("name", "代谢%s")
			.t("desc", "代谢诅咒在穿戴者受伤时能直接将饱腹度转化为生命值，提供额外的治疗，但你会很快陷入饥饿之中。");
	}


	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage) {

		float procChance = 1/6f * procChanceMultiplier(defender);
		if ( Random.Float() < procChance && defender instanceof Hero) {

			//assumes using up 10% of starving, and healing of 1 hp per 10 turns;
			int healing = Math.min((int)Hunger.STARVING/100, defender.HT - defender.HP);

			if (healing > 0) {
				
				Hunger hunger = Buff.affect(defender, Hunger.class);
				
				if (!hunger.isStarving()) {
					
					hunger.affectHunger( healing * -10 );
					
					defender.HP += healing;
					defender.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString( healing ), FloatingText.HEALING);
				}
			}

		}
		
		return damage;
	}

	@Override
	public Glowing glowing() {
		return BLACK;
	}

	@Override
	public boolean curse() {
		return true;
	}
}
