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

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.items.consum.potions.PotionOfHealing;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import pd.messages.InlineText;

public class FrozenCarpaccio extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FrozenCarpaccio.class)
			.t("name", "冷冻生肉片")
			.t("invis", "你看到自己的手隐形了！")
			.t("hard", "你感到皮肤变硬了！")
			.t("refresh", "神清气爽！")
			.t("better", "你感觉好多了！")
			.t("desc", "这是份速冻生肉，只能切成薄片取食，而且意外的好吃。")
			.t("discover_hint", "你可使用另一种食物制作该物品。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = Hunger.HUNGRY/2f;
	}
	
	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		effect(hero);
	}
	
	public int value() {
		return 10 * quantity;
	}

	public static void effect(Hero hero){
		switch (Random.Int( 5 )) {
			case 0:
				GLog.i( Messages.get(FrozenCarpaccio.class, "invis") );
				Buff.affect( hero, Invisibility.class, Invisibility.DURATION );
				break;
			case 1:
				GLog.i( Messages.get(FrozenCarpaccio.class, "hard") );
				Barkskin.conditionallyAppend( hero, hero.HT / 4, 1 );
				break;
			case 2:
				GLog.i( Messages.get(FrozenCarpaccio.class, "refresh") );
				PotionOfHealing.cure(hero);
				break;
			case 3:
				GLog.i( Messages.get(FrozenCarpaccio.class, "better") );
				hero.HP = Math.min( hero.HP + hero.HT / 4, hero.HT );
				hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(hero.HT / 4), FloatingText.HEALING );
				break;
		}
	}
	
	public static Food cook( MysteryMeat ingredient ) {
		FrozenCarpaccio result = new FrozenCarpaccio();
		result.quantity = ingredient.quantity();
		return result;
	}
}
