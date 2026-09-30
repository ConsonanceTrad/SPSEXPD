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
import pd.effects.TargetedCell;
import render.utils.data.SparseArray;
import render.utils.math.Random;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 关卡的生成流程：推入本层随机种子 → 抽取楼层感受与补给 → 反复重置状态调用 build() 直到成功
 * → 铺露珠祝福点 → 重算地块标志位 → 生成怪物（含 SPS 原始怪物标记）与物品 → 弹出随机种子。
 *
 * 从 Level.create() 中独立出来。create() 本身仍留在 Level 作为薄钩子 —— 它被 HallsLevel 覆写
 * 并调 super，所以入口不能移；但流程不必留在关卡基类里。
 *
 * build()/createMobs()/createItems() 仍是 Level 的抽象方法（模板方法的可变部分），
 * markSpsOriginalMobs() 是 SPS 的 protected 钩子，都由本类回调。
 */
public final class LevelGeneration {

	private LevelGeneration() { }

	public static void generate( Level level ) {
		TargetedCell.cells.clear();
		Random.pushGenerator( Dungeon.seedCurDepth() );
		if (level instanceof SpsTriangleLevel) {
			((SpsTriangleLevel)level).prepareLegacyTrial();
		}

		//TODO maybe just make this part of RegularLevel?
		FloorFeeling.apply( level );
		
		int buildAttempts = 0;
		do {
			level.width = level.height = level.length = 0;

			level.transitions = new ArrayList<>();

			level.mobs().clear();
			level.heaps = new SparseArray<>();
			level.blobs = new HashMap<>();
			level.plants = new SparseArray<>();
			level.traps = new SparseArray<>();
			level.customTiles = new ArrayList<>();
			level.customTerrain = new ArrayList<>();
			level.customWalls = new ArrayList<>();
			if (++buildAttempts > 100) {
				Random.popGenerator();
				throw new IllegalStateException("level generation failed after 100 attempts: "
						+ level.getClass().getName() + ", depth=" + Dungeon.depth);
			}
		} while (!level.build());

		SpsDew.place( level );
		
		level.buildFlagMaps();
		CellFlags.cleanWalls( level );
		
		level.createMobs();
		level.markSpsOriginalMobs();
		level.createItems();

		Random.popGenerator();
	}
}
