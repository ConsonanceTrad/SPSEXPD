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
import pd.items.equipment.armor.Armor;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class Brimstone extends Armor.Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Brimstone.class)
			.t("name", "狱火%s")
			.t("desc", "这个刻印会保护穿戴者和其所有物免受火焰的伤害及其他影响。");
	}




	private static ItemSprite.Glowing ORANGE = new ItemSprite.Glowing( 0xFF4400 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {
		//no proc effect, triggers in Char.isImmune
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return ORANGE;
	}

}
