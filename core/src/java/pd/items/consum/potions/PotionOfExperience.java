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

package pd.items.consum.potions;

import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.Flare;
import pd.effects.FloatingText;
import pd.sprites.CharSprite;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class PotionOfExperience extends Potion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfExperience.class)
			.t("name", "经验药剂")
			.t("desc", "众多战斗积累而来的经验被浓缩为液态，这种药剂能够瞬间提升你的等级。");
	}




	{
		icon = ItemIconSheet.POTION_EXP;

		bones = true;

		talentFactor = 2f;
	}
	
	@Override
	public void apply( Hero hero ) {
		identify();
		//SPSEXPD: 改为限时经验增益（docs/alchemy-rework.md §8），不再一次性给一整级经验
		int turns = Math.max(1, Math.round(100 * effectScale()));
		Buff.prolong(hero, pd.actors.buffs.ExpBoost.class, turns);
		hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, "+" + turns, FloatingText.EXPERIENCE);
		new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
	}
	
	@Override
	public int value() {
		return isKnown() ? 50 * quantity : super.value();
	}

	@Override
	public int energyVal() {
		return isKnown() ? 10 * quantity : super.energyVal();
	}
}
