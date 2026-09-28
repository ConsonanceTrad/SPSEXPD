/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;

/**
 * SPS 2D 地形帧映射（全局权威表）。
 *
 * 语义：SPS 0.9.8 的 Terrain 常量值【直接就是】tiles 图集帧号（单层 16x16，见
 * _ref/ref/SPS-PD/java/com/hmdzl/spspd/levels/Terrain.java 与 DungeonTilemap.java）。
 * 本表把 STSSP 的 Terrain 常量（0-19 与 SPS 完全一致；20-54 段为破碎风格重编号）
 * 映射回 SPS 帧号，供回 2D 渲染（DungeonTerrainTilemap / SpsLegacyLevelVisual）共用。
 *
 * 映射来源：
 * - 0-19 基础段与 65-75 SOKOBAN 段：两版常量值一致，直接透传。
 * - 20-54 段：SpsLegacyLevelVisual.terrainVisual 的既有修正表（20+ 关卡已验证）。
 * - 破碎独有常量：SPS 无原生对应帧，按下表映射到语义最近帧（登记待目验）。
 */
public class SpsTerrainFrames {

	//SPS tiles 图集中未被 Terrain 引用的空帧，用作透明/不绘制
	public static final int BLANK = 30;

	/** STSSP Terrain 常量 -> SPS 帧号 */
	public static int visual(int terrain) {
		switch (terrain) {
			//SPS 特有段（STSSP 重编号 -> SPS 帧号）
			case Terrain.GROUND_A:       return 20;
			case Terrain.FLOWER_POT:     return 21;
			case Terrain.WALL_GROUND:    return 22;
			case Terrain.WALL_LIVER:     return 23;
			case Terrain.BUY_WALL:       return 24;
			case Terrain.EMPTY_DECO:     return 25;
			case Terrain.LOCKED_EXIT:    return 26;
			case Terrain.UNLOCKED_EXIT:  return 27;
			case Terrain.WALL_SP:        return 28;
			case Terrain.SIGN:           return 29;
			case Terrain.GLASS_WALL:     return 31;
			case Terrain.OLD_HIGH_GRASS: return 32;
			case Terrain.IRON_MAKER:     return 33;
			case Terrain.WELL:           return 34;
			case Terrain.STATUE:         return 35;
			case Terrain.STATUE_SP:      return 36;
			case Terrain.BROKEN_DOOR:    return 37;
			case Terrain.TENT:           return 38;
			case Terrain.DEW_BLESS:      return 39;
			case Terrain.BED:            return 40;
			case Terrain.BOOKSHELF:      return 41;
			case Terrain.ALCHEMY:        return 42;
			case Terrain.SHRUB:          return 47;
			case Terrain.WATER:          return 63;

			//破碎独有常量 -> 语义最近的 SPS 帧（SPS 无原生帧，登记待目验）
			case Terrain.ENTRANCE_SP:        return 7;    //特殊入口沿用入口帧
			case Terrain.HERO_LKD_DR:        return 10;   //骷髅锁门沿用锁门帧
			case Terrain.FURROWED_GRASS:     return 2;    //踩平的草沿用草地帧
			case Terrain.CRYSTAL_DOOR:       return 31;   //水晶门暂用玻璃墙帧（SPS 无水晶门）
			case Terrain.CUSTOM_DECO:        return BLANK; //隐形装饰由 CustomTilemap 绘制
			case Terrain.CUSTOM_DECO_EMPTY:  return BLANK;
			case Terrain.CUSTOM_DECO_WTR:    return BLANK; //水穿透装饰，水面层在其下
			case Terrain.REGION_DECO:        return 25;   //区域装饰暂用空地装饰帧（B3 随区域图集定标）
			case Terrain.REGION_DECO_ALT:    return 25;
			case Terrain.MINE_CRYSTAL:       return 25;   //挖掘晶体暂用空地装饰帧（B3 定标）
			case Terrain.MINE_BOULDER:       return 25;

			//0-19 基础段与 65-75 SOKOBAN 段两版一致，直接透传
			default: return terrain;
		}
	}
}
