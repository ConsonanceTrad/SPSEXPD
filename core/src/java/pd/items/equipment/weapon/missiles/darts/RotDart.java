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

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Corrosion;
import pd.messages.InlineText;


public class RotDart extends TippedDart {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RotDart.class)
			.t("name", "腐莓飞镖")
			.t("desc", "这种恶毒的飞镖上涂着一种由腐莓制成的酸蚀毒膏，能够蚀穿飞镖触及的任何事物。其上的蚀毒异常强大，寻常敌人触之即死，但强敌可消去其大半功效。相较于其他涂药飞镖，这类飞镖上的涂药药效更加耐用，但其耐用度无法被外力进一步提升。")
			.t("discover_hint", "你可使用某个任务中的种子制作该物品。");
	}



	
	{
		image = ConsumThrowsDict.ROT_DART_0;
	}
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {

		//when processing charged shot, only corrode enemies
		if (processingChargedShot && attacker.alignment == defender.alignment) {
			//do nothing
		} else if (defender.properties().contains(Char.Property.BOSS)
				|| defender.properties().contains(Char.Property.MINIBOSS)){
			Buff.affect(defender, Corrosion.class).set(5f, Dungeon.scalingDepth()/3);
		} else {
			Buff.affect(defender, Corrosion.class).set(10f, Dungeon.scalingDepth());
		}
		
		return super.proc(attacker, defender, damage);
	}
	
	@Override
	public float durabilityPerUse(int level) {
		return MAX_DURABILITY/5f; //always 5 uses
	}
}
