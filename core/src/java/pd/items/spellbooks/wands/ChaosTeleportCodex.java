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

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.utils.GLog;
import pd.utils.Teleport;
import pd.messages.Messages;
import render.noosa.audio.Sample;
import pd.Assets;

/**
 * SPSEXPD: 抄本《混乱传送抄本》（法术书化批次3d）——混乱传送法杖的书化：
 * 把点选位置的目标（无目标则自己）随机扔到本层别处（法力 2）。
 */
public class ChaosTeleportCodex extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(ChaosTeleportCodex.class)
			.t("name", "混乱传送抄本")
			.t("cast_prompt", "选择要传送的目标")
			.t("desc", "薄薄几页，页码是乱的。\n\n施法（消耗 2 法力）：把点选位置的目标（没有目标就是你）_随机传送到本层的另一处_。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public boolean targetRequired() { return true; }

	@Override
	public void castAt( Hero hero, int cell ) {
		Char target = Actor.findChar( cell );
		if (target == null) target = hero;

		Sample.INSTANCE.play( pd.Assets.Sounds.ZAP );

		if (Teleport.teleportChar( target )) {
			GLog.i( Messages.get( ChaosTeleportCodex.class, "tele" ) );
		} else {
			GLog.w( Messages.get( ChaosTeleportCodex.class, "no_tele" ) );
		}
		pd.Dungeon.observe();
	}
}
