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
import pd.actors.buffs.Burning;
import pd.actors.blobs.Fire;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.MagicMissile;
import pd.effects.particles.FlameParticle;
import pd.items.Heap;
import pd.scenes.GameScene;

/**
 * SPSEXPD: 原典《火弹原典》（法术书化批次1）——火弹法杖 5 级曲线：
 * 5~40 伤害 + 点燃 5 回合 + 落点烈焰。
 */
public class FireboltTome extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FireboltTome.class)
			.t("name", "火弹原典")
			.t("cast_prompt", "选择火球轰落的位置")
			.t("desc", "书脊滚烫，翻页时会窜出零星火星。\n\n施法（消耗 5 法力）：向点选位置射出一枚大火球，造成_5~40点伤害_（受_法强_加成），点燃目标 5 回合并把落点烧成火海。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public int castLevel() { return 5; }
	@Override public int min( int level ) { return level; }
	@Override public int max( int level ) { return 10 + 6 * level; }
	@Override protected int missileType() { return MagicMissile.FIRE; }

	@Override
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) {
		Buff.affect( target, Burning.class ).reignite( target, 5f );
		if (target.sprite != null) target.sprite.emitter().burst( FlameParticle.FACTORY, 5 );
	}

	@Override
	protected void onHitCell( Hero hero, int cell, int damage ) {
		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.burn();
		GameScene.add( pd.actors.blobs.Blob.seed( cell, 2, Fire.class ) );
	}
}
