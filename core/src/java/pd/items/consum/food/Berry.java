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

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.CounterBuff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.items.Generator;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class Berry extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Berry.class)
			.t("name", "地牢浆果")
			.t("eat_msg", "酸甜可口，真棒！")
			.t("desc", "这颗小浆果是女猎手在地牢的植被里找到的，它可以被快速食用以获得少量饱腹感，还可能有实用的种子包含其中！")
			.t("discover_hint", "你可使用某项英雄天赋找到该物品。");
	}




	{
		image = ConsumFoodFoodDict.BLACKBERRY;
		energy = 100f; //SPSEXPD: 固定绝对值（原 Hunger.HUNGRY/3f）

		bones = false;
	}

	@Override
	protected float eatingTime(){
		if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH)
				|| Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL)
				|| Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL)
				|| Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL)
				|| Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL)
				|| Dungeon.hero.hasTalent(Talent.ENLIGHTENING_MEAL)){
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		SeedCounter counter = Buff.count(hero, SeedCounter.class, 1);
		if (counter.count() >= 2){
			Dungeon.level.drop(Generator.randomUsingDefaults(Generator.Category.SEED), hero.pos).sprite.drop();
			counter.detach();
		}
	}

	@Override
	public int value() {
		return 5 * quantity;
	}

	public static class SeedCounter extends CounterBuff{{revivePersists = true;}};
}
