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

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.effects.Splash;
import pd.items.equipment.armor.Armor;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Corrosion extends Armor.Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Corrosion.class)
			.t("name", "腐蚀%s")
			.t("desc", "腐蚀诅咒的护甲会喷溅出腐蚀性液体，为区域内的所有东西盖上一层黏糊糊的酸性淤泥。");
	}


	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		float procChance = 1/10f * procChanceMultiplier(defender);
		if ( Random.Float() < procChance ) {
			int pos = defender.pos;
			for (int i : PathFinder.NEIGHBOURS9){
				Splash.at(pos+i, 0x000000, 5);
				if (Actor.findChar(pos+i) != null)
					Buff.affect(Actor.findChar(pos+i), Ooze.class).set( Ooze.DURATION/2 );
			}
		}

		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	@Override
	public boolean curse() {
		return true;
	}
}
