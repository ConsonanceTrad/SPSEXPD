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
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.Callback;

/**
 * SPSEXPD: 终焉法杖（牧师向）。
 *
 * <p>一次性放出**全部剩余充能**，对视野内所有敌人造成伤害；伤害随消耗的充能数放大。
 * 充能耗尽就没有第二次——这是一根「拼一把」的杖。
 */
public class WandOfFinality extends DamageWand {

	static {
		InlineText.of(WandOfFinality.class)
				.t("name", "终焉法杖")
				.t("staff_name", "终焉魔杖")
				.t("desc", "杖顶嵌着一枚缓缓转动的暗色结晶，像一只不肯闭上的眼睛。它不满足于一次一点地施放——只要举起它，你就会想把一切都灌进去。")
				.t("typical_stats_desc", "这根法杖会消耗_全部充能_，对视野内所有敌人造成伤害；每多消耗 1 点充能，伤害就更高。")
				.t("stats_desc", "这根法杖会消耗_全部充能_，对视野内所有敌人造成_%1$d~%2$d点伤害_（按消耗的充能数放大）。")
				.t("no_charge", "终焉法杖没有任何充能可用。")
				.t("used", "你倾尽了终焉法杖的全部充能，%1$d 个敌人在光里化为灰烬。");
	}

	{
		image = SpecificPlaceHolderDict.WAND_HOLDER_0;
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	@Override
	public int min(int lvl) {
		return 6 + 2 * lvl;
	}

	@Override
	public int max(int lvl) {
		return 12 + 4 * lvl;
	}

	@Override
	protected int chargesPerCast() {
		//实际消耗在 onZap 里按当前充能数结算
		return 1;
	}

	@Override
	public void onZap(Ballistica bolt) {
		int spent = Math.max(1, curCharges);
		curCharges = 0;

		Sample.INSTANCE.play(Assets.Sounds.ZAP);
		Sample.INSTANCE.play(Assets.Sounds.BLAST);

		int hits = 0;
		for (Char ch : Actor.chars().toArray(new Char[0])) {
			if (ch == curUser || !ch.isAlive()) continue;
			if (ch.alignment == Char.Alignment.ALLY) continue;
			if (!Dungeon.level.heroFOV[ch.pos]) continue;

			wandProc(ch, spent);
			ch.damage(java.lang.Math.round(damageRoll() * (1f + 0.35f * (spent - 1))), this);
			hits++;
		}

		if (hits == 0) {
			GLog.i(Messages.get(this, "no_charge"));
		} else {
			GLog.p(Messages.get(this, "used", hits));
		}

		updateQuickslot();
	}

	@Override
	public void onHit(pd.items.equipment.weapon.melee.MagesStaff staff, Char attacker, Char defender, int damage) {
		//近战不附带额外效果
	}
}
