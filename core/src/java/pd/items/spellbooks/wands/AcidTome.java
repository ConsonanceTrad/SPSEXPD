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
 * SPSEXPD: 原典《酸蚀原典》（法术书化批次3a）——酸蚀法杖 5 级曲线：
 * 12~26 伤害 + 半数概率挂重酸（5 回合）。
 */
public class AcidTome extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(AcidTome.class)
			.t("name", "酸蚀原典")
			.t("cast_prompt", "选择酸液飞溅的位置")
			.t("desc", "书角被自己的酸液蚀出了圆弧形的缺口。\n\n施法（消耗 5 法力）：向点选位置泼洒强酸，造成_12~26点伤害_（受_法强_加成），并有一半概率腐蚀目标——黏液会持续_5回合_灼蚀皮甲。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public int castLevel() { return 5; }
	@Override public int min( int level ) { return 2 + level; }
	@Override public int max( int level ) { return 6 + 4 * level; }
	@Override protected int missileType() { return MagicMissile.FOLIAGE; }

	@Override
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) {
		if (render.utils.math.Random.Int( 2 ) == 0) {
			Buff.affect( target, Ooze.class ).set( 5 );
		}
	}

	@Override
	protected void onHitCell( Hero hero, int cell, int baseDamage ) {
		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.earthhit();
	}
}
