/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */

package pd.tiles;

import pd.Assets;
import pd.Dungeon;
import pd.levels.CavesLevel;
import pd.levels.CityLevel;
import pd.levels.HallsLevel;
import pd.levels.Level;
import pd.levels.PrisonLevel;
import pd.levels.SewerLevel;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;

/**
 * SPSXPD: 区域装饰（REGION_DECO / REGION_DECO_ALT）的独立渲染层。
 *
 * 这些装饰原先经 SpsTerrainFrames 恒映射到地形图集的第 25 帧（一个通用地面帧），
 * 在各区域都渲染成同一个错误图案。现改用独立图集 environment/tiles/decorate.png：
 * 4 列 x 5 行 = 20 帧，帧号 = 行*4 + 列。
 *   行 = 区域：0 下水道 / 1 监狱 / 2 洞穴 / 3 城市 / 4 恶魔大厅(Halls)
 *   列 = (REGION_DECO 用 0、REGION_DECO_ALT 用 2) + (精致地板/虚空用 1，普通地面用 0)
 * 地形层已把这两个 Terrain 映射为 BLANK，因此本层是它们唯一的绘制者。
 */
public class SpsRegionDecoTilemap extends DungeonTilemap {

	public SpsRegionDecoTilemap(){
		super( Assets.Environment.DECORATE );
		map( Dungeon.level.map, Dungeon.level.width() );
	}

	@Override
	protected int getTileVisual(int pos, int tile, boolean flat) {
		//非装饰格不绘制（地形层负责其余格子）
		if (tile != Terrain.REGION_DECO && tile != Terrain.REGION_DECO_ALT) {
			return -1;
		}

		int row = regionRow();
		if (row < 0) {
			return -1;   //未知区域：不绘制，避免用错帧
		}

		int col = (tile == Terrain.REGION_DECO_ALT) ? 2 : 0;
		//“精致地板/虚空”列：目前只在 ALT 装饰紧邻虚空时启用（如监狱的悬吊牢笼悬于深渊上）
		if (tile == Terrain.REGION_DECO_ALT && nextToChasm(pos)) {
			col += 1;
		}
		return row * 4 + col;
	}

	/** decorate.png 的行号（按关卡区域） */
	private static int regionRow() {
		Level level = Dungeon.level;
		if (level instanceof SewerLevel) return 0;
		if (level instanceof PrisonLevel) return 1;
		if (level instanceof CavesLevel) return 2;
		if (level instanceof CityLevel) return 3;
		if (level instanceof HallsLevel) return 4;
		return -1;
	}

	/** 该格是否紧邻虚空 */
	private static boolean nextToChasm(int pos) {
		int[] m = Dungeon.level.map;
		int w = Dungeon.level.width();
		int len = m.length;
		for (int off : PathFinder.NEIGHBOURS8) {
			int n = pos + off;
			if (n < 0 || n >= len) continue;
			//防止横向绕行到相邻行
			if (Math.abs((n % w) - (pos % w)) > 1) continue;
			if (m[n] == Terrain.CHASM) return true;
		}
		return false;
	}
}