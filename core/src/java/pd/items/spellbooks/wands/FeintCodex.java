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
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.items.consum.scrolls.ScrollOfMirrorImage;
import pd.utils.GLog;
import pd.messages.Messages;

/**
 * SPSEXPD: 抄本《虚影抄本》（法术书化批次3d）——虚影法杖的书化：
 * 在目标附近召出 1 个镜像吸引火力。
 */
public class FeintCodex extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FeintCodex.class)
			.t("name", "虚影抄本")
			.t("cast_prompt", "选择镜像出现的位置")
			.t("desc", "薄薄几页，只画了一个背影。\n\n施法（消耗 2 法力）：在点选位置附近召出_1 个你的镜像_，替你吸引火力。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public boolean targetRequired() { return true; }
	/** 施法等级（照虚影法杖 0 级口径） */
	public int castLevel() { return 0; }

	@Override
	public void castAt( Hero hero, int cell ) {
		int spawned = ScrollOfMirrorImage.spawnImages( hero, cell, 1 + castLevel() / 4 );
		if (spawned <= 0) {
			GLog.w( Messages.get( FeintCodex.class, "failed" ) );
		} else {
			GLog.i( Messages.get( FeintCodex.class, "spawned", spawned ) );
		}
	}
}
