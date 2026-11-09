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
import pd.actors.buffs.Speed;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/**
 * SPSEXPD: 抄本《疾风抄本》（批次E）——耗法力获得一段移速强化（无对应缺失，原典后续批次补）。
 */
public class GaleCodex extends Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(GaleCodex.class)
			.t("name", "疾风抄本")
			.t("desc", "一册薄薄的抄本，页脚的风纹会随阅读轻轻鼓动。\n\n施法（消耗 4 法力）：在 20 回合内获得疾速，移动更加迅捷。\n\n_抄本_：效果稳定，但不及其原典。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override
	public boolean isTome() { return false; }

	@Override
	public int manaCost() { return 4; }

	@Override
	public void onCast( Hero hero ) {
		Buff.affect( hero, Speed.class, 20f );
	}
}
