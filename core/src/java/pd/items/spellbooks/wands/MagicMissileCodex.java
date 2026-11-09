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
import pd.actors.buffs.MagicWeak;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.MagicMissile;

/**
 * SPSEXPD: 抄本《魔弹抄本》（法术书化批次3c）——魔弹法杖 0 级曲线：
 * 2~6 伤害 + 魔法易伤（2 回合）。
 */
public class MagicMissileCodex extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(MagicMissileCodex.class)
			.t("name", "魔弹抄本")
			.t("cast_prompt", "选择魔弹飞出的位置")
			.t("desc", "薄薄几页，字里行间都是最基础的咒式。\n\n施法（消耗 2 法力）：射出一枚小小的魔法飞弹，造成_2~6点伤害_（受_法强_加成）并使目标_受到的魔法伤害轻度提高_（2回合）。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public int castLevel() { return 2; }
	@Override public int min( int level ) { return 2 + level; }
	@Override public int max( int level ) { return 6 + 5 * level; }
	@Override protected int missileType() { return MagicMissile.MAGIC_MISSILE; }
	@Override protected int boltProperties() { return pd.mechanics.Ballistica.MAGIC_BOLT; }

	@Override
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) {
		Buff.affect( target, MagicWeak.class, castLevel() );
		if (target.sprite != null) target.sprite.burst( 0xFF99CCFF, 2 );
	}
}
