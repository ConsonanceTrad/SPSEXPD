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

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.effects.CellEmitter;
import pd.effects.particles.EarthParticle;
import pd.items.equipment.armor.Armor.Glyph;
import pd.items.equipment.armor.Armor;
import pd.plants.Earthroot;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite.Glowing;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Entanglement extends Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Entanglement.class)
			.t("name", "缠绕%s")
			.t("desc", "这个刻印会在使用者周围生出能吸收伤害的地根护甲。这种地根护甲会在使用者移动后散落失效。");
	}



	
	private static ItemSprite.Glowing BROWN = new ItemSprite.Glowing( 0x663300 );
	
	@Override
	public int proc(Armor armor, Char attacker, final Char defender, final int damage ) {

		final int level = Math.max( 0, armor.buffedLvl() );
		float procChance = 1/4f * procChanceMultiplier(defender);

		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);

			Buff.affect( defender, Earthroot.Armor.class ).level( Math.round((5 + 2 * level)*powerMulti) );
			CellEmitter.bottom( defender.pos ).start( EarthParticle.FACTORY, 0.05f, 8 );
			if (defender == Dungeon.hero) PixelScene.shake( 1, 0.4f );
			
		}

		return damage;
	}

	@Override
	public Glowing glowing() {
		return BROWN;
	}
	
}
