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

import pd.Assets;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class PotionOfInvisibility extends Potion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfInvisibility.class)
			.t("name", "隐形药剂")
			.t("invisible", "你看到你的手隐形了！")
			.t("desc", "饮用这种药剂会使你暂时隐形。隐形期间，敌人将无法看见你。使用物理攻击或魔法效果(例如使用卷轴或法杖)，会立即解除隐形效果。");
	}




	{
		icon = ItemIconSheet.POTION_INVIS;
	}

	@Override
	public void apply( Hero hero ) {
		identify();
		Buff.prolong( hero, Invisibility.class, Invisibility.DURATION );
		GLog.i( Messages.get(this, "invisible") );
		Sample.INSTANCE.play( Assets.Sounds.MELD );
	}
	
	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}

}
