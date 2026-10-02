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
import pd.messages.InlineText;

public class ThrowingHammer extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ThrowingHammer.class)
			.t("name", "投掷锤")
			.t("stats_desc", "这件武器不会卡在敌人身上并可被立即收回。")
			.t("desc", "这些重锤是用来扔向敌人的。其光滑的全金属构造让它们非常耐用。");
	}



	
	{
		image = ConsumThrowsDict.THROWING_HAMMER_0;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 0.8f;
		
		tier = 5;
		baseUses = 12;
		sticky = false;
	}

	@Override
	public float pickupDelay() {
		return 0; //picked up instantly
	}

	@Override
	public int max(int lvl) {
		return  4 * tier +                  //20 base, down from 25
				(tier) * lvl;               //scaling unchanged
	}
}
