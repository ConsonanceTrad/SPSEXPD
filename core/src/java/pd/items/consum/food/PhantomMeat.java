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

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.items.consum.potions.PotionOfHealing;
import pd.sprites.CharSprite;
import pd.messages.InlineText;

public class PhantomMeat extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PhantomMeat.class)
			.t("name", "幻影鱼肉")
			.t("desc", "这块从幻影食人鱼身上切下的大块鱼肉呈半透明状，闪烁着奇光。这块充满魔力的肉无须烹饪即可食用，不但能完全填饱你的肚子，而且能赋予多种防御性增益。食用后，它会为你提供隐形、树肤和少量治疗，并净化大部分有害效果。")
			.t("discover_hint", "你可从某种敌人的掉落物中获得该物品。");
	}


	{
		image = ConsumFoodFoodDict.PHANTOM_MEAT;
		energy = Hunger.STARVING;
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		effect(hero);
	}

	public int value() {
		return 30 * quantity;
	}

	public static void effect(Hero hero){

		Barkskin.conditionallyAppend( hero, hero.HT / 4, 1 );
		Buff.affect( hero, Invisibility.class, Invisibility.DURATION );
		hero.HP = Math.min( hero.HP + hero.HT / 4, hero.HT );
		hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(hero.HT / 4), FloatingText.HEALING );
		PotionOfHealing.cure(hero);

	}


}
