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
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.effects.CellEmitter;
import pd.effects.particles.LeafParticle;
import pd.items.Generator;
import pd.items.equipment.armor.Armor;
import pd.plants.Plant;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Overgrowth extends Armor.Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Overgrowth.class)
			.t("name", "丛生%s")
			.t("desc", "丛生诅咒的护甲上生长着各种植物。从美学的角度来讲颇为有趣，但这些花花草草对防御力没有任何贡献，只会在战斗中随机发动效果！");
	}

	
	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		float procChance = 1/20f * procChanceMultiplier(defender);
		if ( Random.Float() < procChance ) {

			Plant p = ((Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED)).couch(defender.pos, null);
			
			//momentarily revoke warden benefits, otherwise this curse would be incredibly powerful
			if (defender instanceof Hero && ((Hero) defender).subClass == HeroSubClass.WARDEN){
				((Hero) defender).subClass = HeroSubClass.NONE;
				p.activate( defender );
				((Hero) defender).subClass = HeroSubClass.WARDEN;
			} else {
				p.activate( defender );
			}
			
			
			CellEmitter.get( defender.pos ).burst( LeafParticle.LEVEL_SPECIFIC, 10 );
			
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
