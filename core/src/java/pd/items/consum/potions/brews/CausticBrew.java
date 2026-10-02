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

package pd.items.consum.potions.brews;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.consum.potions.PotionOfToxicGas;
import pd.items.quest.GooBlob;
import pd.journal.Catalog;
import pd.mechanics.pathfind.PathFinder;
import render.noosa.audio.Sample;
import render.utils.data.BArray;

import java.util.ArrayList;
import pd.messages.InlineText;

public class CausticBrew extends Brew {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CausticBrew.class)
			.t("name", "淤泥魔药")
			.t("desc", "这瓶魔药在打碎时会大范围地溅出腐蚀淤泥。被腐蚀淤泥影响的单位将会缓慢融化，除非他们能及时在水中冲洗掉淤泥。");
	}

	
	{
		image = ConsumPotionSeedBasicPotionDict.BREW_CAUSTIC_0;
	}
	
	@Override
	public void shatter(int cell) {

		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
		}
		
		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 3 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				Splash.at( i, 0x000000, 5);
				Char ch = Actor.findChar(i);
				
				if (ch != null){
					Buff.affect(ch, Ooze.class).set( Ooze.DURATION );
				}
			}
		}
	}
	
	public static class Recipe extends pd.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfToxicGas.class, GooBlob.class};
			inQuantity = new int[]{1, 1};
			
			cost = 1;
			
			output = CausticBrew.class;
			outQuantity = 1;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Catalog.countUse(GooBlob.class);
			return super.brew(ingredients);
		}
	}
}
