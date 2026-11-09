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
import pd.actors.buffs.Frost;
import pd.actors.buffs.FrostIce;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.MagicMissile;
import pd.items.Heap;

/**
 * SPSEXPD: 原典《霜冻原典》（法术书化批次3a）——霜冻法杖 5 级曲线：
 * 15~30 伤害 + 冰甲霜附（或水中三成概率彻底冻结 5×2~4 回合）。
 */
public class FreezeTome extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FreezeTome.class)
			.t("name", "霜冻原典")
			.t("cast_prompt", "选择寒霜袭击的位置")
			.t("desc", "书页翻开时会落下一层细雪。\n\n施法（消耗 5 法力）：向点选位置射出严寒，造成_15~30点伤害_（受_法强_加成）；给目标裹上一层冰壳，而站在水里的目标有_三成概率_被彻底冻结（5×2~4回合）。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public int castLevel() { return 5; }
	@Override public int min( int level ) { return 5 + 2 * level; }
	@Override public int max( int level ) { return 10 + 4 * level; }
	@Override protected int missileType() { return MagicMissile.FROST; }

	@Override
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) {
		if (target.buff( Frost.class ) != null) return; //冻结目标不受影响
		if (target.isAlive()) {
			if (pd.Dungeon.level.water[target.pos]
					&& render.utils.math.Random.Int( 10 ) >= 8 - castLevel()) {
				Buff.affect( target, Frost.class, 5f * render.utils.math.Random.Float( 2f, 4f ) );
			} else {
				Buff.affect( target, FrostIce.class ).level( 5 + castLevel() );
			}
		}
	}

	@Override
	protected void onHitCell( Hero hero, int cell, int baseDamage ) {
		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.freeze();
	}
}
