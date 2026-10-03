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

package pd.items;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;

/**
 * SPS: 徽章 —— 特殊穿戴物，占用独立的徽章槽（与 5 个通用饰品槽互不通用）。
 *
 * 装备/卸下/存档复用 KindofMisc 的通用逻辑；佩戴期间要生效的 buff 由子类
 * 覆写 {@link #buff()} 提供（不需要则返回 null），生命周期与 Ring 一致。
 */
public abstract class Badge extends KindofMisc {

	/** 当前挂到佩戴者身上的 buff，由 activate / doUnequip 维护。 */
	private Buff buff;

	/** 子类覆写：返回本徽章要挂到佩戴者身上的 buff；不需要则返回 null。 */
	protected Buff buff() {
		return null;
	}

	@Override
	public void activate( Char ch ) {
		if (buff != null) {
			buff.detach();
			buff = null;
		}
		buff = buff();
		if (buff != null) {
			buff.attachTo( ch );
		}
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {
			if (buff != null) {
				buff.detach();
				buff = null;
			}
			return true;
		}
		return false;
	}
}
