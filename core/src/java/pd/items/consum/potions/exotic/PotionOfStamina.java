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

import pd.actors.buffs.Buff;
import pd.actors.buffs.Stamina;
import pd.actors.hero.Hero;
import pd.effects.SpellSprite;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class PotionOfStamina extends ExoticPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfStamina.class)
			.t("name", "精力回复合剂")
			.t("desc", "喝下这甜到掉牙的奇怪液体后，体内会爆发一股巨大的能量，让你可以在长时间内飞速奔跑。");
	}



	
	{
		icon = ItemIconSheet.POTION_STAMINA;
	}
	
	@Override
	public void apply(Hero hero) {
		identify();
		
		Buff.prolong(hero, Stamina.class, Stamina.DURATION);
		SpellSprite.show(hero, SpellSprite.HASTE, 0.5f, 1, 0.5f);
	}
	
}
