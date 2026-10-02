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

package pd.actors.buffs;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.equipment.armor.glyphs.AntiMagic;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import pd.messages.InlineText;

public class MagicImmune extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MagicImmune.class)
			.t("name", "魔法免疫")
			.t("desc", "任何魔法都奈何不了你，你对魔法完全免疫。\n\n在魔法免疫状态下，所有魔法效果无论益害都无法影响你，包括但不限于诅咒、附魔、刻印、法杖、卷轴、戒指、神器等。但英雄护甲的能力足够强大，能够不受这种效果影响，正常发挥作用。\n\n魔法免疫剩余时长：%s回合");
	}




	public static final float DURATION = 20f;
	
	{
		type = buffType.POSITIVE;
		announced = true;
	}
	
	{
		immunities.addAll(AntiMagic.RESISTS);
	}

	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)){
			for (Buff b : target.buffs()){
				for (Class immunity : immunities){
					if (b.getClass().isAssignableFrom(immunity)){
						b.detach();
						break;
					}
				}
			}
			if (target instanceof Hero){
				((Hero) target).updateHT(false);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void detach() {
		super.detach();
		if (target instanceof Hero){
			((Hero) target).updateHT(false);
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.COMBO;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0, 1, 0);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
}
