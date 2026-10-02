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

package pd.items.consum.potions.exotic;

import pd.actors.buffs.Barkskin;
import pd.actors.hero.Hero;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class PotionOfEarthenArmor extends ExoticPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfEarthenArmor.class)
			.t("name", "大地护甲合剂")
			.t("desc", "与麻痹药剂不同的是，饮用这瓶合剂能够使使用者的皮肤硬化，在一段时间内形成一道天然护甲。");
	}



	
	{
		icon = ItemIconSheet.POTION_EARTHARMR;
	}
	
	@Override
	public void apply( Hero hero ) {
		identify();
		
		Barkskin.conditionallyAppend( hero, 2 + hero.lvl/3, 50 );
	}
	
}
