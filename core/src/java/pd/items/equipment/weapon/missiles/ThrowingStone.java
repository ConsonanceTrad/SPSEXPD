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

public class ThrowingStone extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ThrowingStone.class)
			.t("name", "投石")
			.t("desc", "这些石头被人用砂纸打磨成趁手的形状，比普通石头更适合大力投向目标。")
			.t("discover_hint", "某位英雄初始携带该物品。");
	}

	
	{
		image = ConsumThrowsDict.THROWING_STONE_0;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.1f;
		
		bones = false;
		
		tier = 1;
		baseUses = 5;
		sticky = false;
	}
	
	@Override
	public int value() {
		return Math.round(super.value()/2f); //half normal value
	}
}
