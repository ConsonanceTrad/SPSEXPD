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

package pd.items.consum.food;

import pd.atlas.items.ConsumFoodFoodDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import pd.messages.InlineText;

public class MysteryMeat extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MysteryMeat.class)
			.t("name", "神秘的肉")
			.t("eat_msg", "这玩意儿吃起来很...奇怪。")
			.t("hot", "嗷！好烫！")
			.t("legs", "你的腿没有知觉了！")
			.t("not_well", "你感觉...不太好。")
			.t("stuffed", "你吃撑了。")
			.t("desc", "想吃可以，后果自腹！")
			.t("discover_hint", "你可从某种敌人的掉落物中获得该物品。")
			.t("placeholder.name", "肉");
	}


	{
		image = ConsumFoodFoodDict.MEAT;
		energy = Hunger.HUNGRY/2f;
		hornValue = 1;
	}
	
	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		effect(hero);
	}

	public int value() {
		return 5 * quantity;
	}

	public static void effect(Hero hero){
		switch (Random.Int( 5 )) {
			case 0:
				GLog.w( Messages.get(MysteryMeat.class, "hot") );
				Buff.affect( hero, Burning.class ).reignite( hero );
				break;
			case 1:
				GLog.w( Messages.get(MysteryMeat.class, "legs") );
				Buff.prolong( hero, Roots.class, Roots.DURATION*2f );
				break;
			case 2:
				GLog.w( Messages.get(MysteryMeat.class, "not_well") );
				Buff.affect( hero, Poison.class ).set( hero.HT / 5 );
				break;
			case 3:
				GLog.w( Messages.get(MysteryMeat.class, "stuffed") );
				Buff.prolong( hero, Slow.class, Slow.DURATION );
				break;
		}
	}
	
	public static class PlaceHolder extends MysteryMeat {
		
		{
			image = SpecificPlaceHolderDict.FOOD_HOLDER_0;
		}
		
		@Override
		public boolean isSimilar(Item item) {
			return item instanceof MysteryMeat || item instanceof StewedMeat
					|| item instanceof ChargrilledMeat || item instanceof FrozenCarpaccio;
		}
		
		@Override
		public String info() {
			return "";
		}
	}
}
