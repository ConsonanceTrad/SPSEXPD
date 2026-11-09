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

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Frost;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.MagicMissile;
import pd.items.Heap;

/**
 * SPSEXPD: 原典《冰霜原典》（法术书化批次1）——冰霜法杖 5 级曲线：
 * 7~33 伤害 + 冻伤（水中更强），冻结目标免疫、已有冻伤衰减伤害（照冰霜法杖口径）。
 */
public class FrostTome extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FrostTome.class)
			.t("name", "冰霜原典")
			.t("cast_prompt", "选择寒霜喷发的位置")
			.t("desc", "书页摸上去冰凉，可指尖始终不会被冻伤。\n\n施法（消耗 5 法力）：向点选位置喷发寒霜，造成_7~33点伤害_（受_法强_加成）并冻伤目标；目标站在水里时冻伤更重。已被冻结的目标不会再次受创，冻伤越重的目标受伤越轻。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public int castLevel() { return 5; }
	@Override public int min( int level ) { return 2 + level; }
	@Override public int max( int level ) { return 8 + 5 * level; }
	@Override protected int missileType() { return MagicMissile.FROST; }

	@Override
	public void castAt( Hero hero, int cell ) {
		//照冰霜法杖口径：先冻地面堆/灭火，再结算伤害
		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.freeze();
		pd.actors.blobs.Fire fire = (pd.actors.blobs.Fire) pd.Dungeon.level.blobs.get( pd.actors.blobs.Fire.class );
		if (fire != null && fire.volume > 0) fire.clear( cell );

		super.castAt( hero, cell );

		if (pd.actors.Actor.findChar( cell ) == null) {
			pd.Dungeon.level.pressCell( cell );
		}
	}

	@Override
	protected int hitDamage( Hero hero, pd.actors.Char target, int damage ) {
		if (target.buff( Frost.class ) != null) return 0; //冻结目标免疫
		Chill chill = target.buff( Chill.class );
		if (chill != null) {
			//6.67% 伤害衰减每层冻伤（照冰霜法杖）
			float turns = Math.min( 10, chill.cooldown() );
			return (int) Math.round( damage * Math.pow( 0.9333f, turns ) );
		}
		return damage;
	}

	@Override
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) {
		if (target.isAlive()) {
			int turns = pd.Dungeon.level.water[target.pos] ? 4 + castLevel() : 2 + castLevel();
			Buff.affect( target, Chill.class, turns );
		}
	}
}
