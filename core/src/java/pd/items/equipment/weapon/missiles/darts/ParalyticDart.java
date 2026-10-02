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

package pd.items.equipment.weapon.missiles.darts;

import pd.atlas.items.ConsumThrowsDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.messages.InlineText;

public class ParalyticDart extends TippedDart {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ParalyticDart.class)
			.t("name", "麻痹飞镖")
			.t("desc", "这些飞镖上涂着一种由地缚根制成的药物，能让目标无助地麻痹一小段时间。");
	}



	
	{
		image = ConsumThrowsDict.PARALYTIC_DART_0;
	}
	
	@Override
	public int proc( Char attacker, Char defender, int damage ) {
		//when processing charged shot, only stun enemies
		if (!processingChargedShot || attacker.alignment != defender.alignment) {
			Buff.prolong(defender, Paralysis.class, 5f);
		}
		return super.proc( attacker, defender, damage );
	}
	
}
