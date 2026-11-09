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

package pd.items.spellbooks;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.messages.InlineText;

/**
 * SPSEXPD: 抄本《帷幕抄本》（批次E）——耗法力获得一段隐身。
 */
public class VeilCodex extends Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(VeilCodex.class)
			.t("name", "帷幕抄本")
			.t("desc", "夹页间垂着一缕化不开的暗色，像一幕随时可以落下的帷幕。\n\n施法（消耗 5 法力）：在 10 回合内隐身。\n\n_抄本_：效果稳定，但不及其原典。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override
	public boolean isTome() { return false; }

	@Override
	public int manaCost() { return 5; }

	@Override
	public void onCast( Hero hero ) {
		Buff.affect( hero, Invisibility.class, 10f );
	}
}
