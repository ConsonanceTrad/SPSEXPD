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

package pd.items.consum.potions.exotic;

import pd.Challenges;
import pd.Dungeon;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.FloatingText;
import pd.items.consum.potions.PotionOfHealing;
import pd.sprites.CharSprite;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class PotionOfShielding extends ExoticPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfShielding.class)
			.t("name", "奥术护盾合剂")
			.t("desc", "与治疗药剂不同的是，饮用这瓶合剂能够使周身被护盾环绕，抵挡所受到的伤害。");
	}

	
	{
		icon = ItemIconSheet.POTION_SHIELDING;
	}
	
	@Override
	public void apply(Hero hero) {
		identify();

		if (Dungeon.isChallenged(Challenges.NO_HEALING)){
			PotionOfHealing.pharmacophobiaProc(hero);
		} else {
			//~75% of a potion of healing
			Buff.affect(hero, Barrier.class).setShield((int) (0.6f * hero.HT + 10));
			hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString((int) (0.6f * hero.HT + 10)), FloatingText.SHIELDING );
		}
	}
}
