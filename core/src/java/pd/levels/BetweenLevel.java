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

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.Tinkerer1;
import pd.items.Amulet;
import pd.items.Heap;
import pd.items.Item;
import pd.items.food.fruit.Blackberry;
import pd.items.food.fruit.Blueberry;
import pd.items.food.fruit.Cloudberry;
import pd.items.food.fruit.Moonberry;
import pd.items.quest.Mushroom;
import pd.levels.builders.Builder;
import pd.levels.features.LevelTransition;
import pd.levels.builders.SpsBetweenBuilder;
import pd.levels.painters.Painter;
import pd.levels.painters.SpsBetweenPainter;
import pd.levels.rooms.Room;
import pd.levels.rooms.secret.SecretRoom;
import pd.levels.rooms.special.ShopRoom;
import pd.levels.rooms.special.SpsShopRoom;
import pd.levels.rooms.special.SpsTentRoom;
import pd.levels.rooms.standard.EmptyRoom;
import pd.levels.rooms.standard.StandardRoom;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.SurfaceScene;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.noosa.Game;
import render.utils.math.Random;

import java.util.ArrayList;

/**
 * SPS-PD's peaceful chapter-transition floor. These floors hold the chapter
 * shop and deliberately contain no ordinary monsters or random floor loot.
 */
public class BetweenLevel extends RegularLevel {

	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
		viewDistance = 6;
	}

	@Override
	protected boolean build() {
		if (!super.build()) return false;
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.SECRET_DOOR) map[cell] = Terrain.DOOR;
		}
		ArrayList<Integer> signCells = new ArrayList<>();
		for (int y = roomEntrance.top + 1; y < roomEntrance.bottom; y++) {
			for (int x = roomEntrance.left + 1; x < roomEntrance.right; x++) {
				int cell = x + y * width();
				if (cell != entrance() && traps.get(cell) == null) signCells.add(cell);
			}
		}
		if (!signCells.isEmpty()) map[Random.element(signCells)] = Terrain.SIGN;
		String legacyTexture = legacyTilesTex();
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				legacyTexture, width(), height(), map));
		return true;
	}

	@Override
	protected int standardRooms(boolean forceMax) {
		return 8;
	}

	@Override
	protected int specialRooms(boolean forceMax) {
		return 0;
	}

	@Override
	protected Builder builder() {
		return new SpsBetweenBuilder();
	}

	@Override
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> result = super.initRooms();
		result.removeIf(room -> room instanceof ShopRoom || room instanceof SecretRoom);
		result.removeIf(room -> room instanceof StandardRoom
				&& room != roomEntrance && room != roomExit);
		for (int i = 0; i < standardRooms(false); i++) {
			result.add(new EmptyRoom());
		}
		result.add(new SpsShopRoom());
		result.add(new SpsTentRoom());
		return result;
	}

	@Override
	protected Painter painter() {
		return new SpsBetweenPainter().setWater(0.35f, 4).setGrass(0.30f, 3);
	}

	@Override
	public String tilesTex() {
		switch (Dungeon.depth) {
			case 0:  return Assets.Environment.TILES_SEWERS;
			case 6:  return Assets.Environment.TILES_PRISON;
			case 11: return Assets.Environment.TILES_CAVES;
			case 16: return Assets.Environment.TILES_CITY;
			default: return Assets.Environment.TILES_HALLS;
		}
	}

	String legacyTilesTex() {
		switch (Dungeon.depth) {
			case 0:  return Assets.Environment.SPS_TILES_SEWERS_LEGACY;   //SPS: 0 层用 SPS 下水道盖图组
			case 6:  return Assets.Environment.SPS_TILES_PRISON_LEGACY;
			case 11: return Assets.Environment.SPS_TILES_BEACH;
			case 16: return Assets.Environment.SPS_TILES_CITY_LEGACY;
			default: return Assets.Environment.SPS_TILES_HALLS_LEGACY;
		}
	}

	@Override
	public String waterTex() {
		switch (Dungeon.depth) {
			case 0:  return Assets.Environment.SPS_WATER_SEWERS;
			case 6:  return Assets.Environment.SPS_WATER_PRISON;
			case 11: return Assets.Environment.SPS_WATER_CAVES;
			case 16: return Assets.Environment.SPS_WATER_CITY;
			default: return Assets.Environment.SPS_WATER_HALLS;
		}
	}

	@Override
	protected void createMobs() {
		// SPS transition floors have no ordinary monster population.
	}

	//SPS: 0 层的入口是通向外界的门（destDepth 为负）。携带护符走过它即为通关；
	//1 层的 SURFACE 只负责把玩家送回 0 层，通关判定统一放在这里。
	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (Dungeon.depth == 0 && transition.destDepth < 0
				&& hero.belongings.getItem( Amulet.class ) != null) {
			Statistics.ascended = true;
			Game.switchScene(SurfaceScene.class, new Game.SceneChangeCallback() {
				@Override
				public void beforeCreate() {
				}

				@Override
				public void afterCreate() {
					Badges.validateHappyEnd();
					Dungeon.win( Amulet.class );
					Dungeon.deleteGame( GamesInProgress.curSlot, true );
					Badges.saveGlobal();
				}
			});
			return true;
		}
		return super.activateTransition(hero, transition);
	}

	@Override
	public Actor addRespawner() {
		return null;
	}

	@Override
	protected void createItems() {
		ArrayList<Integer> nonShopHeaps = new ArrayList<>();
		for (Heap heap : heaps.valueList()) {
			if (heap.type != Heap.Type.FOR_SALE) nonShopHeaps.add(heap.pos);
		}
		for (int cell : nonShopHeaps) heaps.remove(cell);

		//SPS: 0 层 = 特殊初始层（学者+锁出口）；任务蘑菇仅商店购买（地面刷新会消失，不落地）
		if (Dungeon.depth == 0) {
			dropAtRandom(new Moonberry());
			dropAtRandom(new Blueberry());
			dropAtRandom(new Cloudberry());
			dropAtRandom(new Blackberry());
			placeTinkerer();
			Level.set(exit(), Terrain.LOCKED_EXIT, this);
		}
	}

	private void placeTinkerer() {
		ArrayList<Integer> candidates = new ArrayList<>();
		//SPS: 0 层学者贴入口楼梯旁（类似破碎法师 NPC 的出场位置）：先取入口 8 邻格
		for (int n : PathFinder.NEIGHBOURS8) {
			int cell = entrance() + n;
			if (cell >= 0 && cell < length() && passable[cell] && cell != exit()
					&& heaps.get(cell) == null && mobs().findMob(cell) == null) {
				candidates.add(cell);
			}
		}
		if (candidates.isEmpty()) {
			int entranceX = entrance() % width();
			int entranceY = entrance() / width();
			for (int cell = 0; cell < length(); cell++) {
				int x = cell % width();
				int y = cell / width();
				if (Math.abs(x - entranceX) <= 5 && Math.abs(y - entranceY) <= 5
						&& passable[cell] && cell != entrance() && cell != exit()
						&& heaps.get(cell) == null && mobs().findMob(cell) == null) {
					candidates.add(cell);
				}
			}
		}
		if (candidates.isEmpty()) {
			for (int cell = 0; cell < length(); cell++) {
				if (passable[cell] && cell != entrance() && cell != exit()
						&& heaps.get(cell) == null && mobs().findMob(cell) == null) {
					candidates.add(cell);
				}
			}
		}
		if (!candidates.isEmpty()) {
			Tinkerer1 tinkerer = new Tinkerer1();
			tinkerer.pos = Random.element(candidates);
			mobs().add(tinkerer);
		}
	}

	private void dropAtRandom(Item item) {
		drop(item, randomDropCell()).type = Heap.Type.HEAP;
	}

	@Override
	public int randomRespawnCell(Char ch) {
		return -1;
	}
}
