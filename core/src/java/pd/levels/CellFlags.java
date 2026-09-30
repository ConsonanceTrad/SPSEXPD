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
 */

package pd.levels;

import pd.actors.blobs.Blob;
import pd.actors.blobs.Web;
import pd.levels.traps.Trap;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.tiles.CustomTilemap;
import render.utils.geom.Point;

/**
 * 地块标志位地图的构建与增量维护：可通行 / 遮挡视线 / 易燃 / 暗门 / 实心 / 回避 / 液体 / 深坑，
 * 以及大体积生物所需的 openSpace（开阔地）、可发现性 discoverable。
 *
 * 从 Level 中独立出来。Level 里保留了三个薄钩子转发到本类 —— 它们各被 1~2 个子类覆写，
 * 且覆写都调 super（LastLevel/SewerLevel 的 buildFlagMaps 与 updateCellFlags、
 * CavesBossLevel 的 setCellToWater），所以钩子必须留在基类，但算法不必。
 *
 * 标志位数组本身（passable/losBlocking/...）与 map、blobs 一样留在 Level，它们是关卡的地图状态。
 */
public final class CellFlags {

	private CellFlags() { }

	/** 依据地形表重建全部标志位，并把地图边界一圈压成实心。 */
	public static void build( Level level ) {

		for (int i=0; i < level.length(); i++) {
			int flags = Terrain.flags[level.map[i]];
			level.passable[i]     = (flags & Terrain.PASSABLE) != 0;
			level.losBlocking[i]  = (flags & Terrain.LOS_BLOCKING) != 0;
			level.flamable[i]     = (flags & Terrain.FLAMABLE) != 0;
			level.secret[i]       = (flags & Terrain.SECRET) != 0;
			level.solid[i]        = (flags & Terrain.SOLID) != 0;
			level.avoid[i]        = (flags & Terrain.AVOID) != 0;
			level.water[i]        = (flags & Terrain.LIQUID) != 0;
			level.pit[i]          = (flags & Terrain.PIT) != 0;
		}

		for (Blob b : level.blobs.values()){
			b.onBuildFlagMaps(level);
		}

		int lastRow = level.length() - level.width();
		for (int i=0; i < level.width(); i++) {
			level.passable[i] = level.avoid[i] = false;
			level.losBlocking[i] = level.solid[i] = true;
			level.passable[lastRow + i] = level.avoid[lastRow + i] = false;
			level.losBlocking[lastRow + i] = level.solid[lastRow + i] = true;
		}
		for (int i=level.width(); i < lastRow; i += level.width()) {
			level.passable[i] = level.avoid[i] = false;
			level.losBlocking[i] = level.solid[i] = true;
			level.passable[i + level.width()-1] = level.avoid[i + level.width()-1] = false;
			level.losBlocking[i + level.width()-1] = level.solid[i + level.width()-1] = true;
		}

		//an open space is large enough to fit large mobs. A space is open when it is not solid
		// and there is an open corner with both adjacent cells opens
		for (int i=0; i < level.length(); i++) {
			if (level.solid[i]){
				level.openSpace[i] = false;
			} else {
				for (int j = 1; j < PathFinder.CIRCLE8.length; j += 2){
					if (level.solid[i+PathFinder.CIRCLE8[j]]) {
						level.openSpace[i] = false;
					} else if (!level.solid[i+PathFinder.CIRCLE8[(j+1)%8]]
							&& !level.solid[i+PathFinder.CIRCLE8[(j+2)%8]]){
						level.openSpace[i] = true;
						break;
					}
				}
			}
		}

	}

	//updates open space both on the cell itself and adjacent cells
	public static void updateOpenSpace( Level level, int cell ){
		int centerX = cell % level.width();
		int centerY = cell / level.width();
		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {
				int x = centerX + dx;
				int y = centerY + dy;
				if (x < 0 || y < 0 || x >= level.width() || y >= level.height()) continue;
				int target = x + y * level.width();
				if (level.solid[target] || x == 0 || y == 0 || x == level.width() - 1 || y == level.height() - 1){
					level.openSpace[target] = false;
					continue;
				}
				level.openSpace[target] = false;
				for (int j = 1; j < PathFinder.CIRCLE8.length; j += 2){
					if (level.solid[target + PathFinder.CIRCLE8[j]]) {
						level.openSpace[target] = false;
					} else if (!level.solid[target + PathFinder.CIRCLE8[(j+1)%8]]
							&& !level.solid[target + PathFinder.CIRCLE8[(j+2)%8]]){
						level.openSpace[target] = true;
						break;
					}
				}
			}
		}
	}

	/** 烧掉一格：易燃或空地变成余烬，并清掉该格的蛛网。 */
	public static void destroy( Level level, int pos ) {
		//if raw tile type is flammable or empty
		int terr = level.map[pos];
		if (terr == Terrain.EMPTY || terr == Terrain.EMPTY_DECO
				|| (Terrain.flags[level.map[pos]] & Terrain.FLAMABLE) != 0) {
			Level.set(pos, Terrain.EMBERS);
		}
		Blob web = level.blobs.get(Web.class);
		if (web != null){
			web.clear(pos);
		}
	}

	/** 重算哪些格与墙相邻（可被「揭示」）。 */
	public static void cleanWalls( Level level ) {
		if (level.discoverable == null || level.discoverable.length != level.length) {
			level.discoverable = new boolean[level.length()];
		}

		for (int i=0; i < level.length(); i++) {

			boolean d = false;

			for (int j=0; j < PathFinder.NEIGHBOURS9.length; j++) {
				int n = i + PathFinder.NEIGHBOURS9[j];
				if (n >= 0 && n < level.length() && level.map[n] != Terrain.WALL && level.map[n] != Terrain.WALL_DECO) {
					d = true;
					break;
				}
			}

			level.discoverable[i] = d;
		}
	}

	/** 单格地形变了之后同步它的标志位、通知 blob、并刷新该格与邻格的 openSpace。 */
	public static void updateCellFlags( Level level, int cell ){
		int terrain = level.map[cell];

		int flags = Terrain.flags[terrain];
		level.passable[cell]      = (flags & Terrain.PASSABLE) != 0;
		level.losBlocking[cell]   = (flags & Terrain.LOS_BLOCKING) != 0;
		level.flamable[cell]      = (flags & Terrain.FLAMABLE) != 0;
		level.secret[cell]        = (flags & Terrain.SECRET) != 0;
		level.solid[cell]         = (flags & Terrain.SOLID) != 0;
		level.avoid[cell]         = (flags & Terrain.AVOID) != 0;
		level.pit[cell]           = (flags & Terrain.PIT) != 0;
		level.water[cell]         = terrain == Terrain.WATER;

		if (level instanceof SewerLevel){
			if (level.map[cell] == Terrain.REGION_DECO || level.map[cell] == Terrain.REGION_DECO_ALT){
				level.flamable[cell] = true;
			}
		}

		for (Blob b : level.blobs.values()){
			b.onUpdateCellFlags(level, cell);
		}

		updateOpenSpace(level, cell);
	}

	/** 揭示一格暗门或隐藏地形。 */
	public static void discover( Level level, int cell ) {
		Level.set( cell, Terrain.discover( level.map[cell] ) );
		Trap trap = level.traps.get( cell );
		if (trap != null)
			trap.reveal();
		GameScene.updateMap( cell );
	}

	/** 把一格变成水，覆盖在上面的自定义图块可以拒绝。 */
	public static boolean setCellToWater( Level level, boolean includeTraps, int cell ){
		Point p = level.cellToPoint(cell);

		//if a custom tilemap is over that cell, check if it allows water
		for (CustomTilemap cust : level.customTiles){
			Point custPoint = new Point(p);
			custPoint.x -= cust.tileX;
			custPoint.y -= cust.tileY;
			if (custPoint.x >= 0 && custPoint.y >= 0
					&& custPoint.x < cust.tileW && custPoint.y < cust.tileH){
				if (!cust.allowWater(custPoint.x, custPoint.y)){
					return false;
				}
			}
		}

		int terr = level.map[cell];
		if (terr == Terrain.EMPTY || terr == Terrain.GRASS ||
				terr == Terrain.EMBERS || terr == Terrain.EMPTY_SP ||
				terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS
				|| terr == Terrain.EMPTY_DECO){
			Level.set(cell, Terrain.WATER);
			GameScene.updateMap(cell);
			return true;
		} else if (includeTraps && (terr == Terrain.SECRET_TRAP ||
				terr == Terrain.TRAP || terr == Terrain.INACTIVE_TRAP)){
			Level.set(cell, Terrain.WATER);
			level.traps.remove(cell);
			GameScene.updateMap(cell);
			return true;
		}

		return false;
	}
}
