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

import pd.Assets;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.WildRuneMark;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.effects.CellEmitter;
import pd.effects.particles.SparkParticle;
import pd.mechanics.Ballistica;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;

/**
 * SPSEXPD: 狂野符文（法师向）。
 *
 * <p>它自己几乎不造成伤害，只是把符文烙在目标身上。目标之后挨到**任何法杖**的一击就会引爆，
 * 爆炸量以那一击的伤害为基准（比例 20% + 4%/级，由本杖等级决定），并向周围逐格衰减扩散，
 * 扩散伤害同样会引爆附近目标身上的符文。
 */
public class WandOfWildRune extends Wand {

	static {
		InlineText.of(WandOfWildRune.class)
				.t("name", "狂野符文")
				.t("staff_name", "狂野符文")
				.t("desc", "它的杖身像一块没写完的石板，刻痕还在自己移动。它不负责伤害，只负责把「等着」这件事写进别人身上。")
				.t("typical_stats_desc", "这根法杖会给目标烙下_符文_。目标之后被任意法杖命中时引爆，"
						+ "爆炸量为那一击伤害的 _%1$d%%_，并向周围逐格衰减扩散；扩散伤害同样会引炸附近的符文。")
				.t("stats_desc", "这根法杖会给目标烙下_符文_。目标之后被任意法杖命中时引爆，"
						+ "爆炸量为那一击伤害的 _%1$d%%_，并向周围逐格衰减扩散；扩散伤害同样会引炸附近的符文。")
				.t("marked", "符文烙在了目标身上。")
				.t("no_target", "没有可以烙印的目标。");
	}

	{
		image = SpecificPlaceHolderDict.WAND_HOLDER_0;
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	/** 引爆倍率：20% + 4%/级。 */
	public static float ratioForLevel(int level) {
		return 0.20f + 0.04f * Math.max(0, level);
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target == null || target.alignment == (curUser == null ? null : curUser.alignment)) {
			GLog.w(Messages.get(this, "no_target"));
			return;
		}

		Sample.INSTANCE.play(Assets.Sounds.ZAP);
		CellEmitter.get(target.pos).burst(SparkParticle.FACTORY, 6);

		float ratio = ratioForLevel(buffedLvl());
		Buff.affect(target, WildRuneMark.class).setup(ratio, 15f);
		GLog.i(Messages.get(this, "marked"));
	}

	@Override
	public void onHit(pd.items.equipment.weapon.melee.MagesStaff staff, Char attacker, Char defender, int damage) {
		//近战不附带额外效果
	}
}
