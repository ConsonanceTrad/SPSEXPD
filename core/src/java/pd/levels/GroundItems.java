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

import pd.Challenges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.items.Item;
import pd.items.trinkets.TrinketCatalyst;
import pd.items.wands.WandOfRegrowth;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.scenes.GameScene;
import render.utils.math.Random;

/**
 * 在关卡地面上放置内容：待生成物品队列、植物、陷阱。
 *
 * 从 Level 中独立出来：这些操作都只读写关卡的植物/陷阱表和待生成队列，
 * 并且会触发场景表现更新（GameScene.updateMap / plantSeed），不参与任何子类覆写。
 * 注意 Level.drop 仍留在 Level 里 —— 它有 15 个子类覆写，是关卡的多态钩子。
 *
 * plants / traps / itemsToSpawn 三个表本身留在 Level：前者被全仓 138/159 个文件直接访问，
 * 是关卡地图状态而非本机制的私有数据。
 */
public final class GroundItems {

	private GroundItems() { }

	/** 排队一件将在 createItems 阶段落地的物品（先于房间绘制，因此房间能找到它）。 */
	public static void addItemToSpawn( Level level, Item item ) {
		if (item != null) {
			level.itemsToSpawn.add( item );
		}
	}

	public static Item findPrizeItem( Level level ){ return findPrizeItem( level, null ); }

	public static Item findPrizeItem( Level level, Class<?extends Item> match ){
		if (level.itemsToSpawn.size() == 0)
			return null;

		if (match == null){
			//if we have a trinket catalyst, always return that first
			for (Item item : level.itemsToSpawn){
				if (item instanceof TrinketCatalyst){
					level.itemsToSpawn.remove(item);
					return item;
				}
			}

			Item item = Random.element(level.itemsToSpawn);
			level.itemsToSpawn.remove(item);
			return item;
		}

		for (Item item : level.itemsToSpawn){
			if (match.isInstance(item)){
				level.itemsToSpawn.remove( item );
				return item;
			}
		}

		return null;
	}

	public static Plant plant( Level level, Plant.Seed seed, int pos ) {

		Plant plant = level.plants.get( pos );
		if (plant != null) {
			plant.wither();
		}

		if (level.map[pos] == Terrain.HIGH_GRASS ||
				level.map[pos] == Terrain.FURROWED_GRASS ||
				level.map[pos] == Terrain.EMPTY ||
				level.map[pos] == Terrain.EMBERS ||
				level.map[pos] == Terrain.EMPTY_DECO) {
			Level.set(pos, Terrain.GRASS, level);
			GameScene.updateMap(pos);
		}

		//we have to get this far as grass placement has RNG implications in levelgen
		if (Dungeon.isChallenged(Challenges.NO_HERBALISM)){
			return null;
		}

		plant = seed.couch( pos, level );
		level.plants.put( pos, plant );

		GameScene.plantSeed( pos );

		for (Char ch : Actor.chars()){
			if (ch instanceof WandOfRegrowth.Lotus
					&& ((WandOfRegrowth.Lotus) ch).inRange(pos)
					&& Actor.findChar(pos) != null){
				plant.trigger();
				return null;
			}
		}

		return plant;
	}

	/** 与 plant 相同，但种子按 excouch 播种（成长阶段不同）。 */
	public static Plant explant( Level level, Plant.Seed seed, int pos ) {
		Plant plant = level.plants.get(pos);
		if (plant != null) plant.wither();

		if (level.map[pos] == Terrain.HIGH_GRASS || level.map[pos] == Terrain.FURROWED_GRASS
				|| level.map[pos] == Terrain.EMPTY || level.map[pos] == Terrain.EMBERS
				|| level.map[pos] == Terrain.EMPTY_DECO) {
			Level.set(pos, Terrain.GRASS, level);
			GameScene.updateMap(pos);
		}

		plant = seed.excouch(pos, level);
		level.plants.put(pos, plant);
		GameScene.plantSeed(pos);
		return plant;
	}

	public static void uproot( Level level, int pos ) {
		level.plants.remove(pos);
		GameScene.updateMap( pos );
	}

	public static Trap setTrap( Level level, Trap trap, int pos ){
		Trap existingTrap = level.traps.get(pos);
		if (existingTrap != null){
			level.traps.remove( pos );
		}
		trap.set( pos );
		level.traps.put( pos, trap );
		GameScene.updateMap( pos );
		return trap;
	}

	public static void disarmTrap( Level level, int pos ) {
		Level.set(pos, Terrain.INACTIVE_TRAP);
		GameScene.updateMap(pos);
	}
}
