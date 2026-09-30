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

import pd.ShatteredPixelDungeon;
import pd.actors.blobs.Blob;
import pd.effects.TargetedCell;
import pd.items.Heap;
import pd.levels.features.LevelTransition;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.tiles.CustomTilemap;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * 关卡存档的读写：标志位大地图、已探明/已绘制、转场点、地面堆叠物、植物、陷阱、
 * 自定义图块、Blob、楼层感受，以及区域挑战与露珠清层用的计数字段。
 *
 * 从 Level 中独立出来：restoreFromBundle/storeInBundle 是高覆写钩子（约 20 个关卡子类覆写并调
 * super），入口必须留在 Level；但读写逻辑本身可以搬，Level 上只留转发。
 *
 * restore 结尾会重算地块标志位与可发现性——存档里存的是地形原始值，标志位是派生数据。
 */
public final class LevelPersistence {
	private static final String VERSION     = "version";
	private static final String WIDTH       = "width";
	private static final String HEIGHT      = "height";
	private static final String MAP			= "map";
	private static final String VISITED		= "visited";
	private static final String MAPPED		= "mapped";
	private static final String TRANSITIONS	= "transitions";
	private static final String LOCKED      = "locked";
	private static final String HEAPS		= "heaps";
	private static final String PLANTS		= "plants";
	private static final String TRAPS       = "traps";
	private static final String CUSTOM_TILES= "customTiles";
	private static final String CUSTOM_TERRAIN= "customTerrain";
	private static final String CUSTOM_WALLS= "customWalls";
	private static final String BLOBS		= "blobs";
	private static final String FEELING		= "feeling";
	private static final String CURRENT_MOVES = "currentmoves";
	private static final String CLEARED		= "cleared";
	private static final String FORCE_DONE	= "forcedone";
	private static final String PIT_SIGN     = "pit_sign";

	private LevelPersistence() { }

	public static void restore( Level level, Bundle bundle ) {


		level.version = bundle.getInt( VERSION );
		
		//saves from before v3.1.1 are not supported
		if (level.version < ShatteredPixelDungeon.v3_1_1){
			throw new RuntimeException("old save");
		}

		level.setSize( bundle.getInt(WIDTH), bundle.getInt(HEIGHT));
		
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		level.blobs = new HashMap<>();
		level.plants = new SparseArray<>();
		level.traps = new SparseArray<>();
		level.customTiles = new ArrayList<>();
		level.customTerrain = new ArrayList<>();
		level.customWalls = new ArrayList<>();
		
		level.map		= bundle.getIntArray( MAP );

		level.visited	= bundle.getBooleanArray( VISITED );
		level.mapped	= bundle.getBooleanArray( MAPPED );

		level.transitions = new ArrayList<>();
		for (Bundlable b : bundle.getCollection( TRANSITIONS )){
			level.transitions.add((LevelTransition) b);
		}

		level.locked      = bundle.getBoolean( LOCKED );
		level.currentMoves = bundle.getInt( CURRENT_MOVES );
		level.cleared = bundle.getBoolean( CLEARED );
		level.forceDone = bundle.getBoolean( FORCE_DONE );
		level.pitSign = bundle.contains(PIT_SIGN) ? bundle.getInt(PIT_SIGN) : -1;
		
		Collection<Bundlable> collection = bundle.getCollection( HEAPS );
		for (Bundlable h : collection) {
			Heap heap = (Heap)h;
			if (!heap.isEmpty())
				level.heaps.put( heap.pos, heap );
		}
		
		collection = bundle.getCollection( PLANTS );
		for (Bundlable p : collection) {
			Plant plant = (Plant)p;
			level.plants.put( plant.pos, plant );
		}

		collection = bundle.getCollection( TRAPS );
		for (Bundlable p : collection) {
			Trap trap = (Trap)p;
			level.traps.put( trap.pos, trap );
		}

		collection = bundle.getCollection( CUSTOM_TILES );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			level.customTiles.add(vis);
		}

		collection = bundle.getCollection( CUSTOM_TERRAIN );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			level.customTerrain.add(vis);
		}

		collection = bundle.getCollection( CUSTOM_WALLS );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			level.customWalls.add(vis);
		}
		
		level.mobs().restoreFromBundle( bundle );

		collection = bundle.getCollection( BLOBS );
		for (Bundlable b : collection) {
			Blob blob = (Blob)b;
			level.blobs.put( blob.getClass(), blob );
		}

		level.feeling = bundle.getEnum( FEELING, Level.Feeling.class );
		if (level.feeling == Level.Feeling.DARK) {
			level.viewDistance = Math.round(5 * level.viewDistance / 8f);
		}
		TargetedCell.cells.clear();
		if (bundle.contains( "targeted_cells" )){
			collection = bundle.getCollection( "targeted_cells" );
			for (Bundlable c : collection) {
				TargetedCell cell = (TargetedCell)c;
				if (cell != null) {
					TargetedCell.cells.put(cell.pos, cell);
				}
			}
		}

		level.buildFlagMaps();
		CellFlags.cleanWalls( level );

	}

	public static void store( Level level, Bundle bundle ) {
		bundle.put( VERSION, Game.versionCode );
		bundle.put( WIDTH, level.width );
		bundle.put( HEIGHT, level.height );
		bundle.put( MAP, level.map );
		bundle.put( VISITED, level.visited );
		bundle.put( MAPPED, level.mapped );
		bundle.put( TRANSITIONS, level.transitions );
		bundle.put( LOCKED, level.locked );
		bundle.put( CURRENT_MOVES, level.currentMoves );
		bundle.put( CLEARED, level.cleared );
		bundle.put( FORCE_DONE, level.forceDone );
		bundle.put( PIT_SIGN, level.pitSign );
		bundle.put( HEAPS, level.heaps.valueList() );
		bundle.put( PLANTS, level.plants.valueList() );
		bundle.put( TRAPS, level.traps.valueList() );
		bundle.put( CUSTOM_TILES, level.customTiles );
		bundle.put( CUSTOM_TERRAIN, level.customTerrain);
		bundle.put( CUSTOM_WALLS, level.customWalls );
		level.mobs().storeInBundle( bundle );
		bundle.put( BLOBS, level.blobs.values() );
		bundle.put( FEELING, level.feeling );
		bundle.put( "targeted_cells", TargetedCell.cells.valueList() );
	}
}
