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

package pd.items.equipment.armor;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;


public class ClericArmor extends ClassArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ClericArmor.class)
			.t("name", "英雄祭服")
			.t("desc", "穿着这套内着重甲的祭服时，牧师能够施展一项特殊技能。");
	}




	{
		image = EquipmentEquipArmorBasicArmorDict.HERO_ARMOR_ASCETIC;
	}

}
