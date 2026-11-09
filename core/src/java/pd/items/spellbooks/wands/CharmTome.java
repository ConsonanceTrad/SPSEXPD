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

import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Amok;
import pd.actors.buffs.SpsCharm;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.MagicMissile;
import pd.effects.Speck;
import pd.items.Heap;
import render.noosa.audio.Sample;
import pd.Assets;

/**
 * SPSEXPD: 原典《魅惑原典》（法术书化批次3b）——使敌人陷入狂乱并暂时无法攻击施术者
 * （照魅惑法杖口径重写：Amok + SpsCharm，5 级时长）。
 */
public class CharmTome extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(CharmTome.class)
			.t("name", "魅惑原典")
			.t("cast_prompt", "选择魅惑扩散的位置")
			.t("desc", "书签是一枚漆黑的宝石雕成的爱心。\n\n施法（消耗 5 法力）：使点选位置的敌人陷入_狂乱_（见谁打谁），并_暂时无法直接攻击你_（5~15回合）。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	/** 施法等级（照对应法杖的 0/5 级口径） */
	public int castLevel() { return 5; }
	@Override public int manaCost() { return 5; }
	@Override protected int missileType() { return MagicMissile.PURPLE_LIGHT; }

	@Override
	public void castAt( Hero hero, int cell ) {
		Actor target = Actor.findChar( cell );
		if (target instanceof pd.actors.Char) {
			pd.actors.Char ch = (pd.actors.Char) target;
			if (ch == pd.Dungeon.hero) {
				Buff.affect( ch, Vertigo.class, 5f );
			} else {
				Buff.affect( ch, Amok.class, castLevel() + 5 );
				SpsCharm charm = Buff.affect( ch, SpsCharm.class,
						render.utils.math.Random.IntRange( 5, 3 * castLevel() ) );
				charm.object = hero.id();
				if (ch.sprite != null) {
					ch.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
				}
				Sample.INSTANCE.play( pd.Assets.Sounds.CHARMS );
			}
		}

		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.lighthit();
	}
}
