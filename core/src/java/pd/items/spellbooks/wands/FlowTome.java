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
 * SPSEXPD: 原典《涌流原典》（法术书化批次3b）——涌流法杖 5 级曲线：
 * 6~20 伤害 + 打湿 + 强力击退（位移复用 WandOfFlow.throwChar 公共出口，效果链路独立重写）。
 */
public class FlowTome extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FlowTome.class)
			.t("name", "涌流原典")
			.t("cast_prompt", "选择水流冲击的位置")
			.t("desc", "书页翻动时有潮水般的哗哗声。\n\n施法（消耗 5 法力）：向点选位置喷射强劲水流，造成_6~20点伤害_（受_法强_加成），_打湿_目标并把它_狠狠冲退_。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	private int lastSource = -1;

	@Override public boolean isTome() { return true; }
	/** 施法等级（照涌流法杖 5 级口径） */
	public int castLevel() { return 5; }
	@Override public int manaCost() { return 5; }
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
			//击退：沿施术者→目标方向冲退（5 级强度=5；复用涌流法杖的公共位移出口）
			if (target.isAlive()) {
				int dir = knockAwayCell( hero, target );
				if (dir != target.pos) {
					Ballistica trajectory = new Ballistica( target.pos, dir, Ballistica.MAGIC_BOLT );
					WandOfFlow.throwChar( target, trajectory, 5 );
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
