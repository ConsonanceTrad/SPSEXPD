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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pd.items.spellbooks.wands;

import java.util.ArrayList;

import pd.actors.hero.Hero;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 谬误书基类（错误法杖的书化）——施法时从 WandBooks 书对池随机抽一本、
 * 按指定形态（原典/抄本）直接施放（效果链路独立，法力已由谬误书自身扣除）。
 */
public abstract class ErrorSpellbook extends pd.items.spellbooks.Spellbook {

	@Override
	public boolean targetRequired() {
		//落点跟随抽中的书：统一走定向选点（非定向书在 castAt 里忽略落点）
		return true;
	}

	@Override
	public void castAt( Hero hero, int cell ) {
		ArrayList<pd.items.spellbooks.WandBooks.Pair> pairs =
				new ArrayList<>( pd.items.spellbooks.WandBooks.all() );
		if (pairs.isEmpty()) return;
		pd.items.spellbooks.WandBooks.Pair pick = pairs.get( render.utils.math.Random.Int( pairs.size() ) );

		Class<? extends pd.items.spellbooks.Spellbook> cls = isTome() ? pick.tome : pick.codex;
		//谬误书按自己的形态抽——但落点结算的等级用 errorCastLevel 折算：
		//直接实例化抽中的书（其 castLevel 固定），谬误等级差异由书对自身原典/抄本形态体现
		pd.items.spellbooks.Spellbook book = Reflection.newInstance( cls );
		if (book == null) return;
		book.castAt( hero, cell );
	}
}
