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

import pd.actors.buffs.Buff;
import pd.actors.buffs.FrostImbue;
import pd.actors.hero.Hero;
import pd.effects.particles.SnowParticle;
import pd.items.consum.potions.exotic.PotionOfSnapFreeze;
import pd.messages.InlineText;

public class ElixirOfIcyTouch extends Elixir {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ElixirOfIcyTouch.class)
			.t("name", "晶触秘药")
			.t("desc", "饮用后，这瓶秘药会使饮用者获得从敌人身上抽取热量的能力。这样的效果让使用者能够在药效持续期间免疫冻伤，同时他的物理攻击也能对敌人造成冻伤效果。");
	}

	
	{
		image = ConsumPotionSeedBasicPotionDict.ELIXIR_ICY_0;
	}
	
	@Override
	public void apply(Hero hero) {
		Buff.prolong(hero, FrostImbue.class, FrostImbue.DURATION);
		hero.sprite.emitter().burst(SnowParticle.FACTORY, 5);
	}
	
	public static class Recipe extends pd.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfSnapFreeze.class};
			inQuantity = new int[]{1};
			
			cost = 6;
			
			output = ElixirOfIcyTouch.class;
			outQuantity = 1;
		}
		
	}
}
