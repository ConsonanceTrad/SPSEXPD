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
import pd.messages.Messages;
import render.noosa.audio.Sample;
import pd.Assets;

/**
 * SPSEXPD: 抄本《终焉抄本》（法术书化批次3d）——终焉法杖的书化：
 * 对视野内所有敌人各放一记 0 级当量的轻击（6~12 伤害，法强加成）。
 */
public class FinalityCodex extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FinalityCodex.class)
			.t("name", "终焉抄本")
			.t("desc", "薄薄几页，最后一页是空白的——大概那就是「终焉」。\n\n施法（消耗 2 法力）：对_视野内的所有敌人_各放出一记轻击，造成_6~12点伤害_（受_法强_加成）。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	/** 施法等级（照终焉法杖 0 级口径） */
	public int castLevel() { return 0; }

	@Override
	public void onCast( Hero hero ) {
		Sample.INSTANCE.play( pd.Assets.Sounds.ZAP );
		Sample.INSTANCE.play( pd.Assets.Sounds.BLAST );

		int hits = 0;
		for (Char ch : Actor.chars().toArray( new Char[0] )) {
			if (ch == hero || !ch.isAlive()) continue;
			if (ch.alignment == Char.Alignment.ALLY) continue;
			if (pd.Dungeon.level.heroFOV[ch.pos]) {
				int dmg = power( hero, render.utils.math.Random.NormalIntRange( 6 + 2 * castLevel(), 12 + 4 * castLevel() ) );
				ch.damage( dmg, this );
				hits++;
			}
		}
		GLog.i( Messages.get( FinalityCodex.class, "used", hits ) );
	}
}
