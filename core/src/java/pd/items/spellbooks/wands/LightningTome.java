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
import pd.effects.Lightning;
import pd.effects.particles.SparkParticle;
import pd.mechanics.pathfind.PathFinder;

import java.util.ArrayList;

/**
 * SPSEXPD: 原典《雷霆原典》（法术书化批次1）——雷霆法杖 5 级曲线：
 * 10~22 伤害，电弧在相邻目标间连锁（伤害随目标数分散），水中目标还会向周围扩散。
 * 效果照雷霆法杖口径直接重写。
 */
public class LightningTome extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(LightningTome.class)
			.t("name", "雷霆原典")
			.t("cast_prompt", "选择雷霆落下的位置")
			.t("desc", "合上的书页间偶尔会蹦出一粒静电，噼啪作响。\n\n施法（消耗 5 法力）：向点选位置落下雷霆，造成_10~22点伤害_（受_法强_加成）。电弧会在相邻的目标间_连锁_——连锁的目标越多，每个目标分到的伤害越轻；水里还会向四周扩散。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	private final ArrayList<Lightning.Arc> arcs = new ArrayList<>();

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public int castLevel() { return 5; }
	@Override public int min( int level ) { return 5 + level; }
	@Override public int max( int level ) { return Math.round( 10 + level * level / 4f ); }

	@Override
	public void castAt( Hero hero, int cell ) {
		ArrayList<Char> affected = new ArrayList<>();
		collectArcTargets( Actor.findChar( cell ), affected );
		if (affected.isEmpty()) {
			//落点没有目标：只在落点炸出电火花
			if (pd.Dungeon.level.heroFOV[cell]) {
				pd.effects.CellEmitter.center( cell ).burst( SparkParticle.FACTORY, 6 );
			}
			return;
		}

		float multiplier = 0.4f + 0.6f / affected.size();
		int base = render.utils.math.Random.NormalIntRange( min( castLevel() ), max( castLevel() ) );
		int dmg = power( hero, base );

		for (Char target : affected) {
			int dealt = Math.round( dmg * multiplier );
			target.damage( dealt, this );
			if (target.sprite != null) {
				target.sprite.centerEmitter().burst( SparkParticle.FACTORY, 5 );
				target.sprite.flash();
			}
		}

		if (!arcs.isEmpty() && affected.get( 0 ).sprite != null) {
			affected.get( 0 ).sprite.parent.add( new Lightning( arcs, null ) );
		}
		arcs.clear();
	}

	/** 照雷霆法杖的 arc 连锁：四邻递归收集，水中目标向 2 格范围扩散 */
	private void collectArcTargets( Char cur, ArrayList<Char> affected ) {
		if (cur == null || affected.contains( cur )) return;
		affected.add( cur );

		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = cur.pos + offset;
			if (!pd.Dungeon.level.insideMap( cell )) continue;
			Char next = Actor.findChar( cell );
			if (next != null && next.alignment != Char.Alignment.ALLY && !affected.contains( next )) {
				arcs.add( new Lightning.Arc( cur.pos, next.pos ) );
				collectArcTargets( next, affected );
			}
		}

		if (pd.Dungeon.level.water[cur.pos] && !cur.flying) {
			PathFinder.buildDistanceMap( cur.pos, render.utils.data.BArray.not( pd.Dungeon.level.solid, null ), 2 );
			for (int cell = 0; cell < PathFinder.distance.length; cell++) {
				if (PathFinder.distance[cell] == Integer.MAX_VALUE || !pd.Dungeon.level.insideMap( cell )) continue;
				Char next = Actor.findChar( cell );
				if (next == null || next.alignment == Char.Alignment.ALLY || affected.contains( next )) continue;
				arcs.add( new Lightning.Arc( cur.pos, next.pos ) );
				collectArcTargets( next, affected );
			}
		}
	}
}
