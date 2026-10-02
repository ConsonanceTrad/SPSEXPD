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

import pd.Badges;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import pd.messages.InlineText;

public class PotionOfStrength extends Potion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfStrength.class)
			.t("name", "力量药剂")
			.t("msg", "新生的力量在你体内喷薄而出，你现在拥有%d点力量！")
			.t("desc", "这种强力的液体会洗刷你的肌肉，永久性增加1点力量值。");
	}


	{
		icon = ItemIconSheet.POTION_STRENGTH;

		unique = true;

		talentFactor = 2f;
	}
	
	@Override
	public void apply( Hero hero ) {
		identify();

		hero.STR++;
		hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, "1", FloatingText.STRENGTH);

		GLog.p( Messages.get(this, "msg", hero.STR()) );
		
		Badges.validateStrengthAttained();
		Badges.validateDuelistUnlock();
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
