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
 * SPSEXPD: 原典《魔弹原典》（法术书化批次3c）——魔弹法杖 5 级曲线：
 * 12~31 伤害 + 魔法易伤（5 回合）。
 */
public class MagicMissileTome extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(MagicMissileTome.class)
			.t("name", "魔弹原典")
			.t("cast_prompt", "选择魔弹飞出的位置")
			.t("desc", "第一页只有一句话：万物皆可为弹。\n\n施法（消耗 5 法力）：射出纯粹的魔法飞弹，造成_12~31点伤害_（受_法强_加成）并使目标_受到的魔法伤害提高_（5回合）。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public int castLevel() { return 5; }
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
