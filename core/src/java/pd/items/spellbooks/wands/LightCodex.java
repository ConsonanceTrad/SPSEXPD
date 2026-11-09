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
import pd.items.Heap;
import pd.tiles.DungeonTilemap;

/**
 * SPSEXPD: 抄本《强光抄本》（法术书化批次3a）——强光法杖 0 级曲线：
 * 3~6 伤害（亡灵/恶魔 ×1.333）+ 低概率致盲 + 微光照明。
 */
public class LightCodex extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(LightCodex.class)
			.t("name", "强光抄本")
			.t("cast_prompt", "选择光线射出的位置")
			.t("desc", "薄薄几页，白得晃眼。\n\n施法（消耗 2 法力）：向点选位置射出一道亮光，造成_3~6点伤害_（受_法强_加成），有较低概率_致盲_目标；对_亡灵与恶魔_造成更高伤害。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public int castLevel() { return 0; }
	@Override public int min( int level ) { return 3 + level; }
	@Override public int max( int level ) { return 6 + 4 * level; }

	@Override
	public void castAt( Hero hero, int cell ) {
		hero.sprite.parent.add( new Beam.LightRay( hero.sprite.center(),
				DungeonTilemap.raisedTileCenterToWorld( cell ) ) );

		Char target = pd.actors.Actor.findChar( cell );
		if (target != null) {
			int base = render.utils.math.Random.NormalIntRange( min( castLevel() ), max( castLevel() ) );
			int damage = power( hero, base );

			int blindRoll = render.utils.math.Random.Int( 5 + castLevel() );
			boolean blinded = blindRoll >= 3 && blindRoll < 5 + castLevel();
			if (blinded) {
				Buff.prolong( target, Blindness.class, 2f + castLevel() * 0.34f );
				if (target.sprite != null) target.sprite.emitter().burst( Speck.factory( Speck.LIGHT ), 4 );
			}

			if (Char.hasProp( target, Char.Property.DEMONIC ) || Char.hasProp( target, Char.Property.UNDEAD )) {
				damage = Math.round( damage * 1.333f );
			} else if (target.sprite != null) {
				target.sprite.centerEmitter().burst( RainbowParticle.BURST, 6 );
			}

			target.damage( damage, this );

			Buff.prolong( hero, Light.class, 5f + castLevel() );
		}

		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.lighthit();
	}
}
