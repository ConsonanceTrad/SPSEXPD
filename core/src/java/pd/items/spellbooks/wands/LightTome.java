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
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Light;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.Beam;
import pd.effects.Speck;
import pd.effects.particles.RainbowParticle;
import pd.effects.particles.ShadowParticle;
import pd.items.Heap;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import pd.tiles.DungeonTilemap;
import render.noosa.audio.Sample;

/**
 * SPSEXPD: 原典《强光原典》（法术书化批次3a）——强光法杖 5 级曲线：
 * 8~26 伤害（亡灵/恶魔 ×1.333）+ 三成上下概率致盲 + 施法后短暂照明。
 */
public class LightTome extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(LightTome.class)
			.t("name", "强光原典")
			.t("cast_prompt", "选择光束射出的位置")
			.t("desc", "翻开它时，纸页亮得像正午的雪原。\n\n施法（消耗 5 法力）：向点选位置射出一道强光，造成_8~26点伤害_（受_法强_加成），有较高概率_致盲_目标；对_亡灵与恶魔_造成更高伤害。施法后你会短暂地被光芒照亮。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public int castLevel() { return 5; }
	@Override public int min( int level ) { return 3 + level; }
	@Override public int max( int level ) { return 6 + 4 * level; }

	@Override
	public void castAt( Hero hero, int cell ) {
		//照强光法杖口径：LightRay 光束 FX（同步）+ 结算
		hero.sprite.parent.add( new Beam.LightRay( hero.sprite.center(),
				DungeonTilemap.raisedTileCenterToWorld( cell ) ) );

		Char target = pd.actors.Actor.findChar( cell );
		if (target != null) {
			int base = render.utils.math.Random.NormalIntRange( min( castLevel() ), max( castLevel() ) );
			int damage = power( hero, base );

			//致盲：单次 roll ∈ [3, 5+lvl) 照原杖
			int blindRoll = render.utils.math.Random.Int( 5 + castLevel() );
			boolean blinded = blindRoll >= 3 && blindRoll < 5 + castLevel();
			if (blinded) {
				Buff.prolong( target, Blindness.class, 2f + castLevel() * 0.34f );
				if (target.sprite != null) target.sprite.emitter().burst( Speck.factory( Speck.LIGHT ), 6 );
			}

			if (Char.hasProp( target, Char.Property.DEMONIC ) || Char.hasProp( target, Char.Property.UNDEAD )) {
				damage = Math.round( damage * 1.333f );
				if (target.sprite != null) target.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 + castLevel() );
				Sample.INSTANCE.play( pd.Assets.Sounds.BURNING );
			} else if (target.sprite != null) {
				target.sprite.centerEmitter().burst( RainbowParticle.BURST, 10 + castLevel() );
			}

			target.damage( damage, this );

			//施法者获得短暂照明（照原杖 onZap 末尾）
			Buff.prolong( hero, Light.class, 5f + castLevel() );
		}

		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.lighthit();
	}
}
