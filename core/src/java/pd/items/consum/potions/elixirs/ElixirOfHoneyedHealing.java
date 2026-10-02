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

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.mobs.Bee;
import pd.items.Honeypot;
import pd.items.consum.potions.PotionOfHealing;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class ElixirOfHoneyedHealing extends Elixir {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ElixirOfHoneyedHealing.class)
			.t("name", "圣愈蜜药")
			.t("desc", "这瓶秘药不仅有着治愈效果，还混杂了蜂蜜的甜香。饮用后，它会触发与治疗药剂一样的效果并回复少量饥饿，它也能被丢出去用来治疗盟友。\n\n对喜爱蜂蜜的生物使用此物也可能安抚住它们。");
	}

	
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}
	
	@Override
	public void apply(Hero hero) {
		PotionOfHealing.cure(hero);
		PotionOfHealing.heal(hero);
		Buff.affect(hero, Hunger.class).satisfy(Hunger.HUNGRY/2f);
		Talent.onFoodEaten(hero, Hunger.HUNGRY/2f, this);
	}
	
	@Override
	public void shatter(int cell) {
		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
		}
		
		Char ch = Actor.findChar(cell);
		if (ch != null){
			PotionOfHealing.cure(ch);
			PotionOfHealing.heal(ch);
			if (ch instanceof Bee && ch.alignment != curUser.alignment){
				ch.alignment = Char.Alignment.ALLY;
				((Bee)ch).setPotInfo(-1, null);
			}
		}
	}

	//lower values, as it's cheaper to make
	@Override
	public int value() {
		return quantity * 40;
	}

	@Override
	public int energyVal() {
		return quantity * 8;
	}

	public static class Recipe extends pd.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfHealing.class, Honeypot.ShatteredPot.class};
			inQuantity = new int[]{1, 1};
			
			cost = 2;
			
			output = ElixirOfHoneyedHealing.class;
			outQuantity = 1;
		}
		
	}
}
