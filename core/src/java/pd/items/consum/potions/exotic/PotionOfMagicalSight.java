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

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicalSight;
import pd.actors.hero.Hero;
import pd.effects.SpellSprite;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class PotionOfMagicalSight extends ExoticPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfMagicalSight.class)
			.t("name", "魔能透视合剂")
			.t("desc", "饮用这瓶合剂后，你的五感将被提高到一种无法想象的地步，使你能看穿12格以内的墙壁，洞察藏在墙后的事物！");
	}



	
	{
		icon = ItemIconSheet.POTION_MAGISIGHT;
	}
	
	@Override
	public void apply(Hero hero) {
		identify();
		Buff.prolong(hero, MagicalSight.class, MagicalSight.DURATION);
		SpellSprite.show(hero, SpellSprite.VISION);
		Dungeon.observe();
		
	}
	
}
