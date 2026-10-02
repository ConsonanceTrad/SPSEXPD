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


public class MailArmor extends Armor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MailArmor.class)
			.t("name", "链甲")
			.t("desc", "由金属链环环相扣制成的一套结实又不失灵活的护甲。");
	}




	{
		image = EquipmentEquipArmorBasicArmorDict.ARMOR_MAIL_0;
	}
	
	public MailArmor() {
		super( 3 );
	}

}
