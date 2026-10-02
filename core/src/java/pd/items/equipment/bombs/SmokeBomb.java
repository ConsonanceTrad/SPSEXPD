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

import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SmokeScreen;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.utils.data.BArray;
import pd.messages.InlineText;

public class SmokeBomb extends Bomb {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SmokeBomb.class)
			.t("name", "烟雾弹")
			.t("desc", "这枚改造过的炸弹会在爆炸时释放出一阵浓厚的气体烟云。炸弹会在2格范围内造成_%1$d~%2$d点伤害_并释放暗夜迷雾。")
			.t("discover_hint", "你可通过炼金合成该物品。");
	}

	
	{
		image = EquipmentEquipWeaponBombDict.SMOKE_BOMB_0;
	}

	@Override
	protected int explosionRange() {
		return 2;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);

		int centerVolume = 1000; //40*25
		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), explosionRange() );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				GameScene.add( Blob.seed( i, 40, SmokeScreen.class ) );
				centerVolume -= 40;
			}
		}

		//excess volume if some cells were blocked
		if (centerVolume > 0){
			GameScene.add( Blob.seed( cell, centerVolume, SmokeScreen.class ) );
		}
		
	}
	
	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 40);
	}
}
