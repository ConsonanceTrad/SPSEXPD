/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.tiles;

import pd.Assets;
import pd.Dungeon;
import pd.levels.Terrain;

/**
 * SPS: 深渊缝合边的独立渲染层（恢复旧版的 2.5D 立体边缘）。
 *
 * 与 {@link SpsWaterEdgesTilemap} 同一思路：地形层（{@link DungeonTerrainTilemap}）已经把
 * 每个 Terrain 常量直映成单帧，不再做分体/缝合，因此深渊（Terrain.CHASM）相邻陆地时
 * 缺少旧版那条"下沉边缘"。本层只对深渊格补画这一帧。
 *
 * 帧来源：{@link DungeonTileSheet#stitchChasmTile(int)} —— 语义与原版 SPS 0.9.8
 * {@code Floor} 的 pit 缝合一致：看**上方**格的地形决定边缘样式
 *   EMPTY_SP / STATUE_SP      -> CHASM_FLOOR_SP
 *   水                         -> CHASM_WATER
 *   不可缝合（墙等 UNSTITCHABLE）-> CHASM_WALL
 *   其余陆地                   -> CHASM_FLOOR
 * 上方也是深渊时不画（保持纯深渊帧），由地形层负责。
 */
public class SpsChasmEdgesTilemap extends DungeonTilemap {

	public SpsChasmEdgesTilemap(){
		super( chasmTex() );
		map( Dungeon.level.map, Dungeon.level.width() );
	}

	/**
	 * 深渊各形态位于【破碎图集】tiles_*.png（帧号按 xy() 换算，见 DungeonTileSheet），
	 * 而不是地形层用的 SPS 图集，所以这里单独按主题映射一次。
	 */
	private static String chasmTex(){
		String t = Dungeon.level.tilesTex();
		if (Assets.Environment.SPS_TILES_SEWERS_LEGACY.equals(t)) return Assets.Environment.TILES_SEWERS;
		if (Assets.Environment.SPS_TILES_PRISON_LEGACY.equals(t)) return Assets.Environment.TILES_PRISON;
		if (Assets.Environment.SPS_TILES_CAVES_LEGACY.equals(t))  return Assets.Environment.TILES_CAVES;
		if (Assets.Environment.SPS_TILES_CITY_LEGACY.equals(t))   return Assets.Environment.TILES_CITY;
		return Assets.Environment.TILES_HALLS;
	}

	@Override
	protected int getTileVisual(int pos, int tile, boolean flat) {

		//只处理深渊格
		if (tile != Terrain.CHASM){
			return -1;
		}

		//最上一行没有"上方格"
		if (pos - mapWidth < 0){
			return -1;
		}

		int above = map[pos - mapWidth];

		//上方也是深渊（或越界）-> 该格是深渊内部，保持纯深渊帧
		if (above == Terrain.CHASM){
			return -1;
		}

		return DungeonTileSheet.stitchChasmTile( above );
	}
}
