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

package pd.items.consum.potions.brews;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.effects.Splash;
import pd.messages.InlineText;

/**
 * SPSEXPD: 魔药占位实现（docs/alchemy-rework.md §7.3）。
 *
 * <p>标准药水 + 本局两味辅材 → 魔药。其中 7 种魔药的效果尚未实现，
 * 按裁决统一给「+10 护盾」占位，后续再逐个替换 {@link #shatter(int)}。
 */
public abstract class PlaceholderBrew extends Brew {

	{
		//SPSEXPD: 图标统一占位，等全部批次完成后统一绘制
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public void shatter(int cell) {
		Char ch = Actor.findChar(cell);
		if (ch == null) ch = curUser;
		if (ch != null) {
			Buff.affect(ch, Barrier.class).setShield(10);
		}
		if (Dungeon.level != null && Dungeon.level.heroFOV != null
				&& cell >= 0 && cell < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[cell]) {
			Splash.at(cell, 0xFFE066, 10);
		}
	}

	@Override
	public int value() {
		return 60 * quantity;
	}
}
