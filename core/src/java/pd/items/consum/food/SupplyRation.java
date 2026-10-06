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
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.FloatingText;
import pd.items.equipment.artifacts.CloakOfShadows;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.sprites.CharSprite;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class SupplyRation extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SupplyRation.class)
			.t("name", "备用口粮")
			.t("desc", "这是一包由盗贼公会存放，供其成员取用的口粮。这包口粮专门被设计成可快速食用的样式，并且能为成员的隐秘行动提供诸多帮助。\n\n尽管不能像正常口粮一样填饱你，它却能被快速食用，恢复少量生命，并且回复盗贼的暗影斗篷一点神器充能。")
			.t("discover_hint", "你可使用某项特定的英雄天赋找到该物品。");
	}




	{
		image = ConsumFoodFoodDict.SMALL_RATION_PACK;
		energy = 2*Hunger.HUNGRY/3f; //200 food value

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

		if (!pd.actors.hero.perks.BloodShield.convert(hero, 5)) hero.HP = Math.min(hero.HP + 5, hero.HT);
		hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, "5", FloatingText.HEALING );

		CloakOfShadows cloak = hero.belongings.getItem(CloakOfShadows.class);
		if (cloak != null) {
			cloak.directCharge(1);
			ScrollOfRecharging.charge(hero);
		}
	}

	@Override
	public int value() {
		return 10 * quantity;
	}

}
