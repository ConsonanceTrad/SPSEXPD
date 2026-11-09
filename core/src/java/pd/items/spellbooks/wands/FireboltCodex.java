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
 * SPSEXPD: 抄本《火弹抄本》（法术书化批次1）——火弹法杖 0 级曲线：
 * 0~10 伤害 + 点燃 3 回合 + 落点火苗。
 */
public class FireboltCodex extends DamageSpellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FireboltCodex.class)
			.t("name", "火弹抄本")
			.t("cast_prompt", "选择火球飞出的位置")
			.t("desc", "页脚画着歪歪扭扭的小火球。\n\n施法（消耗 2 法力）：向点选位置射出一枚小火球，造成_0~10点伤害_（受_法强_加成），点燃目标 3 回合并点燃落点。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public int castLevel() { return 0; }
	@Override public int min( int level ) { return level; }
	@Override public int max( int level ) { return 10 + 6 * level; }
	@Override protected int missileType() { return MagicMissile.FIRE; }

	@Override
	protected void onHitTarget( Hero hero, pd.actors.Char target, int damage ) {
		Buff.affect( target, Burning.class ).reignite( target, 3f );
		if (target.sprite != null) target.sprite.emitter().burst( FlameParticle.FACTORY, 3 );
	}

	@Override
	protected void onHitCell( Hero hero, int cell, int damage ) {
		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.burn();
		GameScene.add( pd.actors.blobs.Blob.seed( cell, 1, Fire.class ) );
	}
}
