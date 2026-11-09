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
 * SPSEXPD: 抄本《烟幕抄本》（法术书化批次3d）——烟幕法杖的书化：
 * 落点小团烟（失明 6 回合、游荡化），自身隐形 2 回合。
 */
public class SmokeBombCodex extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(SmokeBombCodex.class)
			.t("name", "烟幕抄本")
			.t("cast_prompt", "选择小烟雾散开的位置")
			.t("desc", "薄薄几页，一翻就呛。\n\n施法（消耗 2 法力）：在点选位置炸开一小团烟——周围一圈的敌人_失明_（6回合），你自己_隐形 2 回合_。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	@Override public int manaCost() { return 2; }
	@Override public boolean targetRequired() { return true; }

	@Override
	public void castAt( Hero hero, int cell ) {
		Sample.INSTANCE.play( pd.Assets.Sounds.PUFF );
		CellEmitter.get( cell ).burst( Speck.factory( Speck.WOOL ), 10 );

		int blindTurns = 6;
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

		Buff.affect( hero, Invisibility.class, 2f );

		CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
		pd.Dungeon.observe();
		pd.scenes.GameScene.updateFog();
	}
}
