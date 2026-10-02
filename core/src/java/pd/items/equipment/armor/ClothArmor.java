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

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;


public class ClothArmor extends Armor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ClothArmor.class)
			.t("name", "布甲")
			.t("desc", "这件轻便的护甲能提供最基本的防御。")
			.t("discover_hint", "某位英雄初始携带该物品。");
	}




	{
		image = EquipmentEquipArmorBasicArmorDict.ARMOR_CLOTH_0;

		bones = false; //Finding them in bones would be semi-frequent and disappointing.
	}
	
	public ClothArmor() {
		super( 1 );
	}

}
