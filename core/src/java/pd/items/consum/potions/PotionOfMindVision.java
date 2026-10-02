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

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MindVision;
import pd.actors.hero.Hero;
import pd.effects.SpellSprite;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import pd.messages.InlineText;

public class PotionOfMindVision extends Potion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfMindVision.class)
			.t("name", "灵视药剂")
			.t("see_mobs", "你可以感受到其他生物的存在！")
			.t("see_none", "你能判定现在本层内就只有你一个人。")
			.t("desc", "喝下这个，你的心智将与大范围内的生物精神同调，让你能感受到围墙背后的生体所在。该药剂还能够让你的视野无视身边门墙的阻挡。");
	}


	{
		icon = ItemIconSheet.POTION_MINDVIS;
	}

	@Override
	public void apply( Hero hero ) {
		identify();
		Buff.prolong( hero, MindVision.class, MindVision.DURATION );
		SpellSprite.show(hero, SpellSprite.VISION, 1, 0.77f, 0.9f);
		Dungeon.observe();
		
		if (Dungeon.level.mobs().size() > 0) {
			GLog.i( Messages.get(this, "see_mobs") );
		} else {
			GLog.i( Messages.get(this, "see_none") );
		}
	}
	
	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}
