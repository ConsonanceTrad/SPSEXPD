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
import pd.actors.buffs.Haste;
import pd.actors.hero.Hero;
import pd.effects.SpellSprite;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import pd.messages.InlineText;

public class PotionOfHaste extends Potion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfHaste.class)
			.t("name", "极速药剂")
			.t("energetic", "你感觉充满了活力！")
			.t("desc", "喝下这甜到掉牙的奇怪液体后，体内会爆发一股巨大的能量，让你可以短时间内飞速奔跑。");
	}



	
	{
		icon = ItemIconSheet.POTION_HASTE;
	}
	
	@Override
	public void apply(Hero hero) {
		identify();
		
		GLog.w( Messages.get(this, "energetic") );
		Buff.prolong( hero, Haste.class, Haste.DURATION);
		SpellSprite.show(hero, SpellSprite.HASTE, 1, 1, 0);
	}
	
	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}
}
