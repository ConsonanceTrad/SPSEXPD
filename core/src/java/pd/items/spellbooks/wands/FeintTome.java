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
 * SPSEXPD: 原典《虚影原典》（法术书化批次3d）——虚影法杖的书化：
 * 在目标附近召出 2 个你的镜像吸引火力。
 */
public class FeintTome extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FeintTome.class)
			.t("name", "虚影原典")
			.t("cast_prompt", "选择镜像出现的位置")
			.t("desc", "这本书有两个影子：一个在动，一个没动。\n\n施法（消耗 5 法力）：在点选位置附近召出_2 个你的镜像_，它们会替你吸引火力。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public boolean targetRequired() { return true; }
	/** 施法等级（照虚影法杖 5 级口径） */
	public int castLevel() { return 5; }

	@Override
	public void castAt( Hero hero, int cell ) {
		int count = 1 + castLevel() / 4;
		int spawned = ScrollOfMirrorImage.spawnImages( hero, cell, count );
		if (spawned <= 0) {
			GLog.w( Messages.get( FeintTome.class, "failed" ) );
		} else {
			GLog.i( Messages.get( FeintTome.class, "spawned", spawned ) );
		}
	}
}
