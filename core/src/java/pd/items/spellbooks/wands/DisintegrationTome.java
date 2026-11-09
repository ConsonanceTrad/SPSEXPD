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

import java.util.ArrayList;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.Beam;
import pd.effects.CellEmitter;
import pd.effects.particles.PurpleParticle;
import pd.tiles.DungeonTilemap;
import render.utils.math.Random;

/**
 * SPSEXPD: 原典《解离原典》（法术书化批次3c）——解离法杖 5 级曲线：
 * 穿透光束（射程 7），光束上所有目标按 5+目标数+地形加成的等级曲线受伤。
 */
public class DisintegrationTome extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(DisintegrationTome.class)
			.t("name", "解离原典")
			.t("cast_prompt", "选择光束贯穿的方向")
			.t("desc", "它不解释，它删除。\n\n施法（消耗 5 法力）：射出_穿透一切障碍_的解离光束（射程 7），光束上的所有目标都会受伤——穿过的墙与目标越多，伤害越高（受_法强_加成）。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public int castLevel() { return 5; }
	@Override public int min( int level ) { return 2 + level; }
	@Override public int max( int level ) { return 8 + 4 * level; }

	@Override
	public void castAt( Hero hero, int cell ) {
		//照解离法杖口径：WONT_STOP 穿透光束 + 多目标共享伤害等级
		pd.mechanics.Ballistica beam = new pd.mechanics.Ballistica( hero.pos, cell, pd.mechanics.Ballistica.WONT_STOP );
		int maximum = Math.min( 8, castLevel() + 2 );
		ArrayList<Char> targets = new ArrayList<>();
		int terrainPassed = 2;
		int terrainBonus = 0;

		for (int c : beam.subPath( 1, maximum )) {
			Char t = Actor.findChar( c );
			if (t != null) {
				terrainBonus += terrainPassed / 3;
				terrainPassed %= 3;
				targets.add( t );
			}
			if (pd.Dungeon.level.solid[c]) terrainPassed++;
			CellEmitter.center( c ).burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
		}

		int damageLevel = Math.max( 1, castLevel() + Math.max( 0, targets.size() - 1 ) + terrainBonus );
		hero.sprite.parent.add( new Beam.DeathRay( hero.sprite.center(),
				DungeonTilemap.tileCenterToWorld( beam.path.get( Math.min( beam.dist, maximum ) ) ) ) );
		render.noosa.audio.Sample.INSTANCE.play( pd.Assets.Sounds.RAY );

		for (Char t : targets) {
			int dmg = power( hero, Random.NormalIntRange( min( damageLevel ), max( damageLevel ) ) );
			t.damage( dmg, this );
			if (t.sprite != null) {
				t.sprite.centerEmitter().burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
				t.sprite.flash();
			}
		}
	}
}
