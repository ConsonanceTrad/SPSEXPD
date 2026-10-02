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

import pd.Assets;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FireImbue;
import pd.actors.hero.Hero;
import pd.effects.particles.FlameParticle;
import pd.items.consum.potions.exotic.PotionOfDragonsBreath;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class ElixirOfDragonsBlood extends Elixir {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ElixirOfDragonsBlood.class)
			.t("name", "龙血秘药")
			.t("desc", "饮用后，这瓶秘药会使饮用者的血管里充斥着烈焰的力量。这个效果能让饮用者对火焰完全免疫，并且还能通过物理攻击点燃敌人。");
	}

	
	{
		image = ConsumPotionSeedBasicPotionDict.ELIXIR_DRAGON_0;
	}
	
	@Override
	public void apply(Hero hero) {
		Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
		Sample.INSTANCE.play( Assets.Sounds.BURNING );
		hero.sprite.emitter().burst(FlameParticle.FACTORY, 10);
	}
	
	public static class Recipe extends pd.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfDragonsBreath.class};
			inQuantity = new int[]{1};
			
			cost = 10;
			
			output = ElixirOfDragonsBlood.class;
			outQuantity = 1;
		}
		
	}
}
