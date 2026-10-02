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

package pd.items.consum.potions.elixirs;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.potions.exotic.PotionOfEarthenArmor;
import pd.items.quest.GooBlob;
import pd.journal.Catalog;

import java.util.ArrayList;
import pd.messages.InlineText;

public class ElixirOfArcaneArmor extends Elixir {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ElixirOfArcaneArmor.class)
			.t("name", "抗魔秘药")
			.t("desc", "这瓶秘药会赋予饮用者持续时间很长的魔法抗性。");
	}

	
	{
		image = ConsumPotionSeedBasicPotionDict.ELIXIR_ARCANE_0;
	}
	
	@Override
	public void apply(Hero hero) {
		Buff.affect(hero, ArcaneArmor.class).set(5 + hero.lvl/2, 80);
	}
	
	public static class Recipe extends pd.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfEarthenArmor.class, GooBlob.class};
			inQuantity = new int[]{1, 1};
			
			cost = 8;
			
			output = ElixirOfArcaneArmor.class;
			outQuantity = 1;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Catalog.countUse(GooBlob.class);
			return super.brew(ingredients);
		}
	}
}
