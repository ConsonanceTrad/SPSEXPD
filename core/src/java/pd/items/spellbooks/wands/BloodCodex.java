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
import pd.effects.MagicMissile;
import pd.items.Heap;

/**
 * SPSEXPD: 抄本《血契抄本》（法术书化批次3b）——鲜血法杖 0 级曲线：
 * 0~6 伤害并以其少量回复自身（暗系）。
 */
public class BloodCodex extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(BloodCodex.class)
			.t("name", "血契抄本")
			.t("cast_prompt", "选择腐坏能量落下的位置")
			.t("desc", "薄薄几页，指痕发暗。\n\n施法（消耗 2 法力）：向点选位置释放一小股腐坏能量，造成_0~6点伤害_（受_法强_加成），并以其一部分_回复你自己_的生命。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public int castLevel() { return 0; }
	@Override public int min( int level ) { return level; }
	@Override public int max( int level ) { return 6 + 2 * level; }
	@Override protected int missileType() { return MagicMissile.SHADOW; }

	@Override
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) {
		int base = render.utils.math.Random.NormalIntRange( min( castLevel() ), max( castLevel() ) );
		int healing = render.utils.math.Random.Int( 0, base );
		if (hero.HP < hero.HT && healing > 0) {
			hero.HP = Math.min( hero.HT, hero.HP + healing );
		}
	}

	@Override
	protected void onHitCell( Hero hero, int cell, int baseDamage ) {
		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.darkhit();
	}
}
