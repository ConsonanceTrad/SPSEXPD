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

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.Freezing;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.effects.particles.FlameParticle;
import pd.items.equipment.armor.Armor.Glyph;
import pd.items.equipment.armor.Armor;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.ItemSprite.Glowing;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class AntiEntropy extends Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AntiEntropy.class)
			.t("name", "反熵%s")
			.t("desc", "反熵诅咒与宇宙法则背道而驰，会抽离周遭的热量并汇集到穿戴者身上。这会使穿戴者短暂地燃烧，并冻结周围的一切！");
	}


	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage) {

		float procChance = 1/8f * procChanceMultiplier(defender);
		if ( Random.Float() < procChance ) {

			for (int i : PathFinder.NEIGHBOURS8){
				Freezing.affect(defender.pos+i);
			}

			if (!Dungeon.level.water[defender.pos]) {
				Buff.affect(defender, Burning.class).reignite(defender, 4);
			}
			defender.sprite.emitter().burst( FlameParticle.FACTORY, 5 );

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
