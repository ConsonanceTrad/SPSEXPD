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
 * SPSEXPD: 原典《沼泽原典》（法术书化批次3a）——沼泽法杖 5 级曲线：
 * 5~28 伤害 + 大团沼泽气体（易伤且渐缓）。
 */
public class SwampTome extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(SwampTome.class)
			.t("name", "沼泽原典")
			.t("cast_prompt", "选择沼气爆开的位置")
			.t("desc", "合上书时，指缝里会留下一点泥腥味。\n\n施法（消耗 5 法力）：向点选位置砸出一颗沼泽法球，造成_5~28点伤害_（受_法强_加成）并释放一大团沼泽气体——其中的生物会变得_易伤且越来越迟缓_。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public int castLevel() { return 5; }
	@Override public int min( int level ) { return level; }
	@Override public int max( int level ) { return 8 + 4 * level; }
	@Override protected int missileType() { return MagicMissile.POISON; }

	@Override
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) { /*气体由 blob 持续作用*/ }

	@Override
	protected void onHitCell( Hero hero, int cell, int baseDamage ) {
		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.earthhit();
		GameScene.add( pd.actors.blobs.Blob.seed( cell, 200, pd.actors.blobs.SwampGas.class ) );
	}
}
