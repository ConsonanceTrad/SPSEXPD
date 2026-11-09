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

import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.scenes.GameScene;

/**
 * SPSEXPD: 抄本《沼泽抄本》（法术书化批次3a）——沼泽法杖 0 级曲线：
 * 0~8 伤害 + 一团沼泽气体（易伤且渐缓）。
 */
public class SwampCodex extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(SwampCodex.class)
			.t("name", "沼泽抄本")
			.t("cast_prompt", "选择沼气爆开的位置")
			.t("desc", "薄薄几页，带着潮乎乎的土腥气。\n\n施法（消耗 2 法力）：向点选位置砸出一颗小法球，造成_0~8点伤害_（受_法强_加成）并释放一团沼泽气体——其中的生物会变得_易伤且越来越迟缓_。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public int castLevel() { return 0; }
	@Override public int min( int level ) { return level; }
	@Override public int max( int level ) { return 8 + 4 * level; }
	@Override protected int missileType() { return MagicMissile.POISON; }

	@Override
	protected void onHitCell( Hero hero, int cell, int baseDamage ) {
		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.earthhit();
		GameScene.add( pd.actors.blobs.Blob.seed( cell, 100, pd.actors.blobs.SwampGas.class ) );
	}
}
