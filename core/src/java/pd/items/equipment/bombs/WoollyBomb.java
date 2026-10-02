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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items.equipment.bombs;

import pd.atlas.items.EquipmentEquipWeaponBombDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.mobs.npcs.Sheep;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;
import render.utils.data.BArray;

import java.util.ArrayList;
import pd.messages.InlineText;

public class WoollyBomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WoollyBomb.class)
			.t("name", "绵绵炸弹")
			.t("desc", "这枚改造过的炸弹会在爆炸后创造出魔法羊群。爆炸会对2格范围内的所有单位造成_%1$d~%2$d点伤害_。羊群会阻挡单位的移动，并会存在相当长一段时间！然而，羊群无法在强敌面前存在太久，而且羊群也可以通过与之互动被人为地提前消除。")
			.t("discover_hint", "你可通过炼金合成该物品。");
	}



	
	{
		image = EquipmentEquipWeaponBombDict.WOOLY_BOMB_0;
	}

	@Override
	protected int explosionRange() {
		return 2;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);
		
		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), explosionRange()+2 );
		ArrayList<Integer> spawnPoints = new ArrayList<>();
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				spawnPoints.add(i);
			}
		}

		for (int i : spawnPoints){
			if (Dungeon.level.insideMap(i)
					&& Actor.findChar(i) == null
					&& !(Dungeon.level.pit[i])) {
				Sheep sheep = new Sheep();
				sheep.initialize(Dungeon.bossLevel() ? 20 : 200);
				sheep.pos = i;
				GameScene.add(sheep);
				Dungeon.level.occupyCell(sheep);
				CellEmitter.get(i).burst(Speck.factory(Speck.WOOL), 4);
			}
		}
		
		Sample.INSTANCE.play(Assets.Sounds.PUFF);
		Sample.INSTANCE.play(Assets.Sounds.SHEEP);
		
	}
	
	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 30);
	}
}
