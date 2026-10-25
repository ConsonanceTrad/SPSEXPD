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

package pd.items.equipment.wands;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.consum.scrolls.ScrollOfMirrorImage;
import pd.mechanics.Ballistica;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

/**
 * SPSEXPD: 虚影法杖（决斗者向）。
 *
 * <p>把决斗护甲技能「虚招」的欺骗性做成法杖：在目标附近召出一个你的镜像，
 * 它会替你吸引火力。护甲技能本体保留不动。
 */
public class WandOfFeint extends Wand {

	static {
		InlineText.of(WandOfFeint.class)
				.t("name", "虚影法杖")
				.t("staff_name", "虚影魔杖")
				.t("desc", "杖身上刻满了同一个姿势的两个影子——一个在动，一个没动。你很难说清哪个才是它真正指过的方向。")
				.t("typical_stats_desc", "这根法杖会在目标附近召出一个你的_镜像_，它会替你吸引火力。")
				.t("stats_desc", "这根法杖会在目标附近召出_%1$d 个镜像_，它们会替你吸引火力。")
				.t("failed", "虚影没能成形。");
	}

	{
		image = SpecificPlaceHolderDict.WAND_HOLDER_0;
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char user = curUser;
		if (!(user instanceof Hero)) return;

		int count = 1 + buffedLvl() / 4;
		int spawned = ScrollOfMirrorImage.spawnImages((Hero) user, bolt.collisionPos, count);
		if (spawned <= 0) {
			GLog.w(Messages.get(this, "failed"));
		} else {
			GLog.i(Messages.get(this, "stats_desc", spawned));
		}
	}

	@Override
	public void onHit(pd.items.equipment.weapon.melee.MagesStaff staff, Char attacker, Char defender, int damage) {
		//近战不附带额外效果
	}
}
