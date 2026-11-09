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
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.effects.particles.SmokeParticle;
import pd.levels.Terrain;
import render.utils.math.Random;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;

/**
 * SPSEXPD: 抄本《陨星抄本》（法术书化批次3c）——陨星法杖 0 级曲线：
 * 0~12 撞击伤害 + 半数概率短麻痹 + 3×3 微弱溅射与烧焦地形。
 */
public class MeteoriteCodex extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(MeteoriteCodex.class)
			.t("name", "陨星抄本")
			.t("cast_prompt", "选择碎石落下的位置")
			.t("desc", "薄薄几页，夹着一粒真正的小陨石碎屑。\n\n施法（消耗 2 法力）：在点选位置召下一颗小陨石：撞击造成_0~12点伤害_（受_法强_加成），有半数概率_短暂麻痹_目标；3×3 范围有微弱溅射，可燃地形会被烧焦。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public int castLevel() { return 0; }
	@Override public int min( int level ) { return level; }
	@Override public int max( int level ) { return 12 + 6 * level; }

	@Override
	public void castAt( Hero hero, int cell ) {
		int center = cell;
		pd.items.Heap heap = pd.Dungeon.level.heaps.get( center );
		if (heap != null) heap.firehit();
		render.noosa.audio.Sample.INSTANCE.play( pd.Assets.Sounds.ROCKS );

		Char target = Actor.findChar( center );
		int base = Random.NormalIntRange( min( castLevel() ), max( castLevel() ) );
		int mainDamage = power( hero, base );
		if (target != null) {
			if (target.sprite != null) target.sprite.flash();
			target.damage( mainDamage, this );
			if (target.isAlive() && Random.Int( 2 ) == 0) {
				Buff.prolong( target, Paralysis.class, 5 );
			}
			CellEmitter.get( center ).start( Speck.factory( Speck.ROCK ), 0.07f, 4 );
			PixelScene.shake( 2, 0.15f );
		}

		int splash = Math.max( 1, Math.round( mainDamage / 9f ) );
		int width = pd.Dungeon.level.width();
		int cx = center % width;
		int cy = center / width;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				int x = cx + dx;
				int y = cy + dy;
				if (x < 0 || x >= width || y < 0 || y >= pd.Dungeon.level.height()) continue;
				int c = x + y * width;
				if (pd.Dungeon.level.heroFOV[c]) {
					CellEmitter.get( c ).burst( SmokeParticle.FACTORY, 2 );
				}
				if (pd.Dungeon.level.insideMap( c )
						&& (pd.Dungeon.level.flamable[c] || pd.Dungeon.level.map[c] == Terrain.GLASS_WALL)) {
					pd.levels.Level.set( c, Terrain.EMBERS, pd.Dungeon.level );
					GameScene.updateMap( c );
				}
				Char splashTarget = Actor.findChar( c );
				if (splashTarget != null && splashTarget != hero) {
					splashTarget.damage( splash, this );
				}
			}
		}
	}
}
