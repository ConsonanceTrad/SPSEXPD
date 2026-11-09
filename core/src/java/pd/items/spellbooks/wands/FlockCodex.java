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
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.utils.data.BArray;

/**
 * SPSEXPD: 抄本《羊群抄本》（法术书化批次3b）——在点选位置召唤一只小魔法绵羊
 * （持续 2 回合，阻挡移动、受击反击；照招羊法杖口径重写）。
 */
public class FlockCodex extends pd.items.spellbooks.Spellbook {

	//SPSEXPD: inline Chinese text
	static {
		pd.messages.InlineText.of(FlockCodex.class)
			.t("name", "羊群抄本")
			.t("cast_prompt", "选择绵羊落下的位置")
			.t("desc", "薄薄几页，绒毛做的书签。\n\n施法（消耗 2 法力）：在点选位置召唤一只_魔法绵羊_（持续 2 回合）——它杀不死，只会挡路，被打还会_反击_。\n\n_抄本_：常规法术的入门形态。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
	}

	@Override public boolean isTome() { return false; }
	/** 施法等级（照对应法杖的 0/5 级口径） */
	public int castLevel() { return 0; }
	@Override public int manaCost() { return 2; }

	@Override
	public void castAt( Hero hero, int cell ) {
		int spawnCell = findSpawnCell( cell );
		if (spawnCell >= 0) {
			if (hero.subClass == HeroSubClass.LEADER) {
				pd.items.equipment.wands.WandOfFlock.MagicBombSheep sheep = new pd.items.equipment.wands.WandOfFlock.MagicBombSheep();
				sheep.pos = spawnCell;
				GameScene.add( sheep );
				pd.Dungeon.level.occupyCell( sheep );
			} else {
				pd.items.equipment.wands.WandOfFlock.MagicSheep sheep = new pd.items.equipment.wands.WandOfFlock.MagicSheep();
				sheep.pos = spawnCell;
				sheep.initialize( 2f + 0, 0 );
				GameScene.add( sheep );
				pd.Dungeon.level.occupyCell( sheep );
			}
			CellEmitter.get( spawnCell ).burst( Speck.factory( Speck.WOOL ), 4 );
		}

		Heap heap = pd.Dungeon.level.heaps.get( cell );
		if (heap != null) heap.darkhit();
	}

	/** 与招羊法杖同口径的落点寻找（可选位：通行/可避开、无人占、距目标 1 格内） */
	private int findSpawnCell( int target ) {
		boolean[] passable = BArray.or( pd.Dungeon.level.passable, pd.Dungeon.level.avoid, null );
		for (pd.actors.Char ch : pd.actors.Actor.chars()) passable[ch.pos] = false;
		PathFinder.buildDistanceMap( target, passable, 1 );

		int distance = Actor.findChar( target ) == null ? 0 : 1;
		if (distance == 1) PathFinder.distance[target] = Integer.MAX_VALUE;
		for (int cell = 0; cell < pd.Dungeon.level.length(); cell++) {
			if (PathFinder.distance[cell] == distance && pd.Dungeon.level.insideMap( cell )
					&& !pd.Dungeon.level.pit[cell] && Actor.findChar( cell ) == null) return cell;
		}
		return -1;
	}
}
