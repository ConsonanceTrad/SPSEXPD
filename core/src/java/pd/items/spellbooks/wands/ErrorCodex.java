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

import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;

/**
 * SPSEXPD: 抄本《谬误抄本》（法术书化批次3c）——错误法杖的书化：
 * 施法时从已登记书对池里随机抽一本、按其抄本形态施放（法力 2）。
 */
public class ErrorCodex extends ErrorSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(ErrorCodex.class)
			.t("name", "谬误抄本")
			.t("cast_prompt", "选择错误发生的位置")
			.t("desc", "薄薄几页，每一页抄的都不一样。\n\n施法（消耗 2 法力）：发生一个小错误——它会随机变成另一本常规法术书的_抄本形态_施放。\n\n_抄本_：……这大概也是个错误。");
	}

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
}
