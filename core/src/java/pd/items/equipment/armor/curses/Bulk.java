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
import pd.effects.particles.ShadowParticle;
import pd.items.equipment.armor.Armor;
import pd.levels.Terrain;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class Bulk extends Armor.Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Bulk.class)
			.t("name", "臃肿%s")
			.t("desc", "臃肿诅咒的护甲看上去更加厚重，但实际上防御力并没有增强。硕大的体积反而让使用者难以挤入门道。");
	}



	
	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {
		//no proc effect, triggers in Char.speed()
		return damage;
	}

	//more of a reduction really
	public static float speedBoost( Char owner, int level ){
		if (level == -1 ||
				(Dungeon.level.map[owner.pos] != Terrain.DOOR && Dungeon.level.map[owner.pos] != Terrain.OPEN_DOOR )) {
			return 1;
		} else {
			if (owner.sprite != null){
				owner.sprite.emitter().startDelayed(ShadowParticle.UP, 0.02f, 5, 0.05f);
			}
			return 1/3f * genericProcChanceMultiplier(owner);
		}
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
