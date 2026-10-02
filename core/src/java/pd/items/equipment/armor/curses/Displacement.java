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
import pd.items.equipment.armor.Armor;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Displacement extends Armor.Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Displacement.class)
			.t("name", "定相%s")
			.t("desc", "定相诅咒会在穿戴者受到攻击时试图将其传送到安全地带。不过，过强的威力反倒会导致使用者被随机传送到层内各处。");
	}




	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage ) {

		float procChance = 1/20f * procChanceMultiplier(defender);
		if ( Random.Float() < procChance ) {
			ScrollOfTeleportation.teleportChar(defender);
			return 0;
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
