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
import pd.effects.Speck;
import pd.items.equipment.armor.Armor;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Flow extends Armor.Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Flow.class)
			.t("name", "涌流%s")
			.t("desc", "这个刻印能操控使用者周身的水流，让使用者在水中移动时速度大大加快。");
	}




	private static ItemSprite.Glowing BLUE = new ItemSprite.Glowing( 0x0000FF );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {
		//no proc effect, triggers in Char.speed()
		return damage;
	}

	public static float speedBoost( Char owner, int level ){
		if (level == -1 || !Dungeon.level.water[owner.pos]){
			return 1;
		} else {
			if (owner.sprite != null && owner.sprite.visible){
				int particles = 2 + (int) Random.Float(1+level/2f);
				owner.sprite.emitter().startDelayed(Speck.factory(Speck.BLUE_LIGHT), 0.02f, particles, 0.05f);
			}
			return (2f + 0.5f*level) * genericProcChanceMultiplier(owner);
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLUE;
	}

}
