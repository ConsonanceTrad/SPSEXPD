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
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.mechanics.pathfind.PathFinder;
import render.noosa.audio.Sample;
import pd.Assets;

/**
 * SPSEXPD: 原典《烟幕原典》（法术书化批次3d）——烟幕法杖的书化：
 * 落点炸开烟幕（9 格敌人失明 11 回合、游荡化），自身隐形 4 回合。
 */
public class SmokeBombTome extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(SmokeBombTome.class)
			.t("name", "烟幕原典")
			.t("cast_prompt", "选择烟幕炸开的位置")
			.t("desc", "打开它时，文字会先散成灰再排回原位。\n\n施法（消耗 5 法力）：在点选位置炸开烟幕——周围一圈的敌人_失明_（11回合）并迷失目标，你自己则获得_4回合隐形_，方便脱身。\n\n_原典_：常规法术的完整形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.ARTIFACT_SPELLBOOK;
	}

	@Override public boolean isTome() { return true; }
	@Override public int manaCost() { return 5; }
	@Override public boolean targetRequired() { return true; }
	/** 施法等级（照烟幕法杖 5 级口径） */
	public int castLevel() { return 5; }

	@Override
	public void castAt( Hero hero, int cell ) {
		Sample.INSTANCE.play( pd.Assets.Sounds.PUFF );
		CellEmitter.get( cell ).burst( Speck.factory( Speck.WOOL ), 12 );

		int blindTurns = 6 + castLevel();
		for (int i : PathFinder.NEIGHBOURS9) {
			Actor ch = Actor.findChar( cell + i );
			if (ch == null || !(ch instanceof pd.actors.Char) || !((pd.actors.Char) ch).isAlive()) continue;
			if (((pd.actors.Char) ch).alignment == hero.alignment) continue;
			Buff.prolong( (pd.actors.Char) ch, Blindness.class, blindTurns );
			if (ch instanceof pd.actors.mobs.Mob
					&& ((pd.actors.mobs.Mob) ch).state == ((pd.actors.mobs.Mob) ch).HUNTING) {
				((pd.actors.mobs.Mob) ch).state = ((pd.actors.mobs.Mob) ch).WANDERING;
			}
		}

		int invisTurns = 2 + castLevel() / 2;
		Buff.affect( hero, Invisibility.class, invisTurns );

		CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.WOOL ), 8 );
		pd.Dungeon.observe();
		pd.scenes.GameScene.updateFog();
	}
}
