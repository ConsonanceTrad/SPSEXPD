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
 * SPSEXPD: 原典《终焉原典》（法术书化批次3d）——终焉法杖的书化：
 * 对视野内所有敌人各放一发 5 级当量的终结一击（16~32 伤害，法强加成）。
 */
public class FinalityTome extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FinalityTome.class)
			.t("name", "终焉原典")
			.t("desc", "书顶嵌着一枚缓缓转动的暗色结晶，像一只不肯闭上的眼睛。\n\n施法（消耗 5 法力）：对_视野内的所有敌人_各放出一记终结打击，造成_16~32点伤害_（受_法强_加成）。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	/** 施法等级（照终焉法杖 5 级口径） */
	public int castLevel() { return 5; }

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
		GLog.i( Messages.get( FinalityTome.class, "used", hits ) );
	}
}
