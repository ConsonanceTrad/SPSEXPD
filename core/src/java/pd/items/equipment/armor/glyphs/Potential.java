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

package pd.items.equipment.armor.glyphs;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.particles.EnergyParticle;
import pd.items.equipment.armor.Armor.Glyph;
import pd.items.equipment.armor.Armor;
import pd.sprites.ItemSprite.Glowing;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Potential extends Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Potential.class)
			.t("name", "电势%s")
			.t("desc", "这个刻印在被击中时会积蓄能量，在生效时为使用者的法杖充能。");
	}



	
	private static ItemSprite.Glowing WHITE = new ItemSprite.Glowing( 0xFFFFFF, 0.6f );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage) {

		int level = Math.max( 0, armor.buffedLvl() );
		
		// lvl 0 - 16.7%
		// lvl 1 - 28.6%
		// lvl 2 - 37.5%
		float procChance = (level+1f)/(level+6f) * procChanceMultiplier(defender);
		if (Random.Float() < procChance && defender instanceof Hero) {

			float powerMulti = Math.max(1f, procChance);

			int wands = ((Hero) defender).belongings.charge( powerMulti );
			if (wands > 0) {
				defender.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);
			}
		}
		
		return damage;
	}

	@Override
	public Glowing glowing() {
		return WHITE;
	}
}
