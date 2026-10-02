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
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.messages.InlineText;


public class BlindingDart extends TippedDart {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BlindingDart.class)
			.t("name", "致盲飞镖")
			.t("desc", "这些飞镖上涂着一种由致盲草制成的药物，能让目标失明一小段时间。药效比新鲜的致盲草弱，不会让目标迷失方向，所以目标会记住你之前在的位置。");
	}



	
	{
		image = ConsumThrowsDict.BLINDING_DART_0;
	}
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {

		//when processing charged shot, only blind enemies
		if (!processingChargedShot || attacker.alignment != defender.alignment) {
			Buff.affect(defender, Blindness.class, Blindness.DURATION);
		}
		
		return super.proc(attacker, defender, damage);
	}
}
