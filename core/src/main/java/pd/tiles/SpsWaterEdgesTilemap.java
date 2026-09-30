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

package pd.tiles;

import pd.Assets;
import pd.Dungeon;
import pd.levels.Terrain;
import watabou.utils.PathFinder;

/**
 * SPS: 水缝合边的独立渲染层。
 *
 * 水不再取地形图集（tilesTex）的 48-63 帧段：帧号语义随地形图集体系而异
 * （SPS 图集 48 段=水；破碎图集 48 段=物件），混用会把水渲染成铁砧/雕像等图标。
 * 本层绑定每区域独立的水缝合图集（sps_water_edges_*.png，与 sps_water_*.png 成对），
 * 帧号 0-15 直接等于缝合 bit，语义对齐 SPS Floor.paintWaterEdges：
 * 邻格不可与水缝合(UNSTITCHABLE)则置位，+1 上 +2 右 +4 下 +8 左。
 * 水中心（四邻皆不可缝 = 帧 15）跳过，露出下层 SkinnedBlock 水面。
 */
public class SpsWaterEdgesTilemap extends DungeonTilemap {

	public SpsWaterEdgesTilemap(){
		super( waterEdgesTex() );
		map( Dungeon.level.map, Dungeon.level.width() );
	}

	@Override
	protected int getTileVisual(int pos, int tile, boolean flat) {
		if (tile != Terrain.WATER) {
			return -1;   //非水格不绘制
		}
		int t = 0;
		if (unstitchable(pos + PathFinder.CIRCLE4[0])) t += 1;
		if (unstitchable(pos + PathFinder.CIRCLE4[1])) t += 2;
		if (unstitchable(pos + PathFinder.CIRCLE4[2])) t += 4;
		if (unstitchable(pos + PathFinder.CIRCLE4[3])) t += 8;
		return t;
	}

	@Override
	protected boolean needsRender(int pos) {
		if (map[pos] != Terrain.WATER) {
			return false;
		}
		//水中心帧（15，四邻皆不可缝）交给水面层渲染
		return getTileVisual(pos, map[pos], true) != 15;
	}

	//SPS: 不可与水缝合的地形（对照 SPS Terrain.UNSTITCHABLE 的分配：SOLID 全体 + 以下散项）
	private boolean unstitchable(int pos) {
		if (pos < 0 || pos >= map.length) return true;
		int t = map[pos];
		if (t == Terrain.WATER) return true;
		if ((Terrain.flags[t] & Terrain.SOLID) != 0) return true;
		switch (t) {
			case Terrain.CHASM:
			case Terrain.OPEN_DOOR:
			case Terrain.PEDESTAL:
			case Terrain.EMPTY_SP:
			case Terrain.GROUND_A:
			case Terrain.FLOWER_POT:
			case Terrain.STATUE_SP:
			case Terrain.BROKEN_DOOR:
			case Terrain.BOOKSHELF:
				return true;
			default:
				return false;
		}
	}

	//SPS: 区域 → 水缝合图集（与 sps_water_*.png 成对；破碎系 water0-4 也归入对应区域）
	private static String waterEdgesTex(){
		String w = Dungeon.level.waterTex();
		if (w == null) return Assets.Environment.SPS_WATER_EDGES_HALLS;
		switch (w){
			case Assets.Environment.SPS_WATER_SEWERS:
			case Assets.Environment.WATER_SEWERS:
				return Assets.Environment.SPS_WATER_EDGES_SEWERS;
			case Assets.Environment.SPS_WATER_PRISON:
			case Assets.Environment.WATER_PRISON:
				return Assets.Environment.SPS_WATER_EDGES_PRISON;
			case Assets.Environment.SPS_WATER_CAVES:
			case Assets.Environment.WATER_CAVES:
				return Assets.Environment.SPS_WATER_EDGES_CAVES;
			case Assets.Environment.SPS_WATER_CITY:
			case Assets.Environment.WATER_CITY:
				return Assets.Environment.SPS_WATER_EDGES_CITY;
			case Assets.Environment.SPS_WATER_SNOW:
				return Assets.Environment.SPS_WATER_EDGES_SNOW;
			case Assets.Environment.SPS_WATER_HONEY:
				return Assets.Environment.SPS_WATER_EDGES_HONEY;
			default:
				return Assets.Environment.SPS_WATER_EDGES_HALLS;
		}
	}
}
