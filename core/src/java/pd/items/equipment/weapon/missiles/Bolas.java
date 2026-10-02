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

package pd.items.equipment.weapon.missiles;

import pd.atlas.items.ConsumThrowsDict;

import pd.Assets;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class Bolas extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Bolas.class)
			.t("name", "流星索")
			.t("stats_desc", "这件武器能使目标残废。")
			.t("desc", "这种造型特殊的远程武器造成的伤害不高，但能够有效迟滞目标的移动。");
	}



	
	{
		image = EquipmentEquipWeaponBasicWeaponDict.SLING;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;
		
		tier = 3;
		baseUses = 5;
	}

	@Override
	public int min(int lvl) {
		return  2 * (tier-1) +                  //4 base, down from 6
				0*lvl;                          //0 scaling, down from 1
	}

	@Override
	public int max(int lvl) {
		return  3 * tier +                      //9 base, down from 15
				(tier-1)*lvl;                   //2 scaling, down from 3
	}
	
	@Override
	public int proc( Char attacker, Char defender, int damage ) {
		Buff.prolong( defender, Cripple.class, Cripple.DURATION/2 );
		return super.proc( attacker, defender, damage );
	}
}
