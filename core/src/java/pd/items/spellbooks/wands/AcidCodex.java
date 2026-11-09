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

import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.MagicMissile;
import pd.items.Heap;

/**
 * SPSEXPD: 抄本《酸蚀抄本》（法术书化批次3a）——酸蚀法杖 0 级曲线：
 * 2~6 伤害 + 三分之一概率挂轻酸（2 回合）。
 */
public class AcidCodex extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(AcidCodex.class)
			.t("name", "酸蚀抄本")
			.t("cast_prompt", "选择酸液飞溅的位置")
			.t("desc", "薄薄几页，酸渍斑斑。\n\n施法（消耗 2 法力）：向点选位置泼出少量酸液，造成_2~6点伤害_（受_法强_加成），并有三分之一概率轻微腐蚀目标（黏液持续_2回合_）。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public int castLevel() { return 0; }
	@Override public int min( int level ) { return 2 + level; }
	@Override public int max( int level ) { return 6 + 4 * level; }
	@Override protected int missileType() { return MagicMissile.FOLIAGE; }

	@Override
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) {
		if (render.utils.math.Random.Int( 3 ) == 0) {
			Buff.affect( target, Ooze.class ).set( 2 );
		}
	}

	@Override
	protected void onHitCell( Hero hero, int cell, int baseDamage ) {
		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.earthhit();
	}
}
