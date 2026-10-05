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

import pd.Dungeon;
import pd.items.Dewdrop;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.features.LevelTransition;
import pd.tiles.custom.SpsFeatureVisual;
import render.utils.geom.Point;
import render.utils.math.Random;

import java.util.ArrayList;

/**
 * SPS-PD 原创的「露珠祝福」机制：在主地牢楼层入口附近选一格铺上祝福点（踩上去给露珠），
 * 以及该层露珠数量的统计口径。
 *
 * 从 Level 中独立出来：这一族只读写关卡地形与自定义图块，不参与覆写。
 * 相关的 markSpsOriginalMobs 仍留在 Level —— 它被 SpsRegularLevel 覆写，是关卡的钩子。
 */
public final class SpsDew {

	private SpsDew() { }

	public static void place( Level level ) {
		if (!(Dungeon.dewDraw || Dungeon.dewWater) || Dungeon.branch != 0
				|| Dungeon.depth <= 1 || Dungeon.depth >= 25 || Dungeon.bossLevel()
				|| Dungeon.shopOnLevel() || level instanceof BetweenLevel) {
			return;
		}
		int entrance = level.entrance();
		if (!level.insideMap(entrance)) return;
		ArrayList<Integer> candidates = new ArrayList<>();
		int ex = entrance % level.width();
		int ey = entrance / level.width();
		for (int radius = 1; radius <= 4 && candidates.isEmpty(); radius++) {
			for (int y = Math.max(1, ey - radius); y <= Math.min(level.height() - 2, ey + radius); y++) {
				for (int x = Math.max(1, ex - radius); x <= Math.min(level.width() - 2, ex + radius); x++) {
					if (Math.max(Math.abs(x - ex), Math.abs(y - ey)) != radius) continue;
					int cell = x + y * level.width();
					int terrain = level.map[cell];
					if ((terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_DECO
							|| terrain == Terrain.GRASS || terrain == Terrain.EMBERS)
							&& level.traps.get(cell) == null && level.plants.get(cell) == null
							&& !insideTransition(level, cell)) {
						candidates.add(cell);
					}
				}
			}
		}
		if (!candidates.isEmpty()) {
			int cell = Random.element(candidates);
			level.map[cell] = Terrain.DEW_BLESS;
			SpsFeatureVisual visual = new SpsFeatureVisual(SpsFeatureVisual.DEW_BLESS);
			visual.pos(cell, level);
			level.customTiles.add(visual);
		}
	}

	//本关卡内的地形查询不能走 getTransition(cell)/LevelTransition.inside(int)：
	//二者按 Dungeon.level 换算坐标，而本方法在 Level.create() 期间执行，此时 Dungeon.level 尚未指向本关卡
	private static boolean insideTransition( Level level, int cell ) {
		Point p = level.cellToPoint(cell);
		for (LevelTransition transition : level.transitions) {
			if (transition.inside(p)) return true;
		}
		return false;
	}

	public static boolean isClearable( Level level ) {
		return Dungeon.branch == 0 && Dungeon.depth > 1 && Dungeon.depth < 25
				&& !Dungeon.bossLevel() && !(level instanceof BetweenLevel);
	}

	public static boolean hasDew( Level level ) {
		for (Heap heap : level.heaps.valueList()) {
			for (Item item : heap.items) {
				if (item instanceof Dewdrop) return true;
			}
		}
		return false;
	}

	//SPSEXPD: 清层限时已取消，原有的 par() 回合基准一并移除
}
