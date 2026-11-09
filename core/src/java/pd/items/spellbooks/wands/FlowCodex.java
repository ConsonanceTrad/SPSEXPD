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
import pd.actors.buffs.Buff;
import pd.actors.buffs.Wet;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.mechanics.Ballistica;
import pd.items.equipment.wands.fusion.WandOfFlow;

/**
 * SPSEXPD: 抄本《涌流抄本》（法术书化批次3b）——涌流法杖 0 级曲线：
 * 1~5 伤害 + 打湿 + 轻度击退。
 */
public class FlowCodex extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FlowCodex.class)
			.t("name", "涌流抄本")
			.t("cast_prompt", "选择水流洒出的位置")
			.t("desc", "薄薄几页，被水汽洇得发皱。\n\n施法（消耗 2 法力）：向点选位置喷出水流，造成_1~5点伤害_（受_法强_加成），_打湿_目标并把它_轻轻推远_。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	private int lastSource = -1;

	@Override public boolean isTome() { return false; }
	/** 施法等级（照涌流法杖 0 级口径） */
	public int castLevel() { return 0; }
	@Override public int manaCost() { return 2; }
	@Override public boolean targetRequired() { return true; }
	@Override protected int missileType() { return MagicMissile.FROST; }

	@Override
	public void castAt( Hero hero, int cell ) {
		lastSource = hero.pos;

		pd.actors.Char target = Actor.findChar( cell );
		if (target != null && target != hero) {
			int base = render.utils.math.Random.NormalIntRange( 1 + castLevel(), 5 + 3 * castLevel() );
			int dmg = power( hero, base );
			target.damage( dmg, this );
			if (target.sprite != null) target.sprite.flash();

			Buff.affect( target, Wet.class, 5f );
			//击退：0 级强度=3
			if (target.isAlive()) {
				int dir = knockAwayCell( hero, target );
				if (dir != target.pos) {
					Ballistica trajectory = new Ballistica( target.pos, dir, Ballistica.MAGIC_BOLT );
					WandOfFlow.throwChar( target, trajectory, 3 );
				}
			}
		}

		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.icehit();
	}

	private int knockAwayCell( Hero hero, pd.actors.Char target ) {
		int w = pd.Dungeon.level.width();
		int dx = Integer.compare( target.pos % w - lastSource % w, 0 );
		int dy = Integer.compare( target.pos / w - lastSource / w, 0 );
		int cell = target.pos + dy * w + dx;
		return (cell == target.pos) ? target.pos : cell;
	}
}
