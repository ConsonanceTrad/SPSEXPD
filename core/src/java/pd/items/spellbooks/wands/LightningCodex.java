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
import pd.effects.particles.SparkParticle;

/**
 * SPSEXPD: 抄本《雷霆抄本》（法术书化批次1）——雷霆法杖 0 级曲线：
 * 5~10 伤害，单体电击（抄本不连锁）。
 */
public class LightningCodex extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(LightningCodex.class)
			.t("name", "雷霆抄本")
			.t("cast_prompt", "选择闪电落下的位置")
			.t("desc", "书钉是一枚铜夹，静电总把它吸得很紧。\n\n施法（消耗 2 法力）：向点选位置劈下一道小闪电，造成_5~10点伤害_（受_法强_加成）。抄本的力量不足以让电弧连锁。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public int castLevel() { return 0; }
	@Override public int min( int level ) { return 5 + level; }
	@Override public int max( int level ) { return Math.round( 10 + level * level / 4f ); }

	@Override
	protected void onHitTarget( Hero hero, Char target, int damage ) {
		if (target.sprite != null) {
			target.sprite.centerEmitter().burst( SparkParticle.FACTORY, 5 );
			target.sprite.flash();
		}
	}
}
