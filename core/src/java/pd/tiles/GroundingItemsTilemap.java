/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.tiles;

import pd.Dungeon;
import pd.atlas.IconEntry;
import pd.atlas.items.GroundGroundingItemsDict;
import pd.levels.Terrain;

/**
 * SPSXPD: 花盆（FLOWER_POT）/ 炼金釜（ALCHEMY）/ 铁砧（IRON_MAKER）的物件叠加层。
 *
 * 底层地板由地形层按当前图集族绘制（{@code DungeonTerrainTilemap} / {@code SpsLegacyLevelVisual}
 * 都经 {@link DungeonTileSheet#groundItemFloor()} 取「该族地板帧」），本层只把物件图标叠上去，
 * 图标来自 items 图集 {@code sprites/items/ground/grounding_items.png}（单一真源 = 图集元数据
 * {@code tools/atlas-meta/items/ground/grounding_items/_atlas.json}，经 {@link GroundGroundingItemsDict} 引用）。
 *
 * 取帧用「整格」而不是条目里的内容包围盒：包围盒是 trim 结果，直接对齐格左上角会让图标丢格内偏移。
 * 派生层——不进 {@code Level.customTiles}（不序列化），旧存档恢复后天然不会缺层。
 */
public class GroundingItemsTilemap extends DungeonTilemap {

	//帧号自编（仅本层内部使用），不依赖图集宽度
	private static final int FRAME_FLOWER_POT = 0;
	private static final int FRAME_ALCHEMY    = 1;
	private static final int FRAME_ANVIL      = 2;

	public GroundingItemsTilemap() {
		super( GroundGroundingItemsDict.FLOWER_POT.atlas );

		frame( FRAME_FLOWER_POT, GroundGroundingItemsDict.FLOWER_POT );
		frame( FRAME_ALCHEMY,    GroundGroundingItemsDict.ALCHEMY_CAULDRON );
		frame( FRAME_ANVIL,      GroundGroundingItemsDict.ANVIL );

		map( Dungeon.level.map, Dungeon.level.width() );
	}

	/** 把条目的内容包围盒扩展成「它所在的那一格」 */
	private void frame( int id, IconEntry entry ) {
		int left = entry.x(0) - entry.x(0) % SIZE;
		int top  = entry.y(0) - entry.y(0) % SIZE;
		tileset.add( id, left, top, left + SIZE, top + SIZE );
	}

	@Override
	protected int getTileVisual(int pos, int tile, boolean flat) {
		switch (tile) {
			case Terrain.FLOWER_POT: return FRAME_FLOWER_POT;
			case Terrain.ALCHEMY:    return FRAME_ALCHEMY;
			case Terrain.IRON_MAKER: return FRAME_ANVIL;
			default:                 return -1;   //其余格子由地形层负责
		}
	}
}
