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
import pd.actors.buffs.Adrenaline;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class AdrenalineDart extends TippedDart {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AdrenalineDart.class)
			.t("name", "激素飞镖")
			.t("desc", "这些飞镖上涂着一种由速行蓟制成的药物。当目标为友方时，其移动速度和攻击速度都会有所提升，若为敌方则会短暂降低其移动速度。这只飞镖仍能对敌人造成伤害，但不会伤及盟友。");
	}



	
	{
		image = ConsumThrowsDict.ADRENALINE_DART_0;
	}

	@Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			if (((Hero) owner).attackTarget().alignment == owner.alignment){
				return 0; //does not deal damage to allies
			}
		}
		return super.damageRoll(owner);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {

		if (processingChargedShot && defender == attacker) {
			//do nothing to the hero when processing charged shot
		} else if (attacker.alignment == defender.alignment){
			Buff.prolong( defender, Adrenaline.class, Adrenaline.DURATION);
			return 0; //also skips on-hit fx like enchants for allies
		} else {
			Buff.prolong( defender, Cripple.class, Cripple.DURATION/2);
		}
		
		return super.proc(attacker, defender, damage);
	}
}
