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
 * SPSEXPD: 抄本《冰霜抄本》（法术书化批次1）——冰霜法杖 0 级曲线：
 * 2~8 伤害 + 冻伤（水中更强），冻结目标免疫（照冰霜法杖口径）。
 */
public class FrostCodex extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FrostCodex.class)
			.t("name", "冰霜抄本")
			.t("cast_prompt", "选择寒霜喷发的位置")
			.t("desc", "薄薄几页，边角结着一层不会融化的霜花。\n\n施法（消耗 2 法力）：向点选位置喷出寒霜，造成_2~8点伤害_（受_法强_加成）并轻度冻伤目标；目标站在水里时冻伤更重。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public int castLevel() { return 0; }
	@Override public int min( int level ) { return 2 + level; }
	@Override public int max( int level ) { return 8 + 5 * level; }
	@Override protected int missileType() { return MagicMissile.FROST; }

	@Override
	public void castAt( Hero hero, int cell ) {
		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.freeze();

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
			float turns = Math.min( 10, chill.cooldown() );
			return (int) Math.round( damage * Math.pow( 0.9333f, turns ) );
		}
		return damage;
	}

	@Override
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) {
		if (target.isAlive()) {
			int turns = pd.Dungeon.level.water[target.pos] ? 3 : 2;
			Buff.affect( target, Chill.class, turns );
		}
	}
}
