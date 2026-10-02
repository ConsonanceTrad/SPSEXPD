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
import pd.actors.mobs.Piranha;
import pd.messages.InlineText;

public class FishingSpear extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FishingSpear.class)
			.t("name", "捕鱼矛")
			.t("desc", "本用于捕鱼，但作为武器也相当趁手的短投矛。");
	}

	
	{
		image = ConsumThrowsDict.FISHING_SPEAR_0;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.1f;
		
		tier = 2;
	}
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (defender instanceof Piranha){
			damage = Math.max(damage, defender.HP/2);
		}
		return super.proc(attacker, defender, damage);
	}
}
