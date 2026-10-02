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
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.Speck;
import pd.items.equipment.armor.Armor;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Swiftness extends Armor.Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Swiftness.class)
			.t("name", "迅捷%s")
			.t("desc", "这个刻印会在近范围内没有敌人时提高使用者的移动速度。");
	}


	private static ItemSprite.Glowing YELLOW = new ItemSprite.Glowing( 0xFFFF00 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {
		//no proc effect, triggers in Char.speed()
		return damage;
	}

	public static float speedBoost( Char owner, int level ){
		if (level == -1){
			return 1;
		}

		boolean enemyNear = false;
		//an enemy counts as 'near' if they are within a 3-tile passable path of the hero
		//yes this does mean that things like visible trap tiles and chasms count as walls
		PathFinder.buildDistanceMap(owner.pos, Dungeon.level.passable, 3);
		for (Char ch : Actor.chars()){
			if (ch.alignment == Char.Alignment.ENEMY && PathFinder.distance[ch.pos] != Integer.MAX_VALUE){
				enemyNear = true;
			}
		}
		if (enemyNear){
			return 1;
		} else {
			if (owner.sprite != null && owner.sprite.visible){
				int particles = 1 + (int)Random.Float(1+level/5f);
				owner.sprite.emitter().startDelayed(Speck.factory(Speck.YELLOW_LIGHT), 0.02f, particles, 0.05f);
			}
			return (1.2f + 0.04f * level) * genericProcChanceMultiplier(owner);
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return YELLOW;
	}

}
