/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.levels.builders;

import pd.levels.rooms.Room;
import pd.levels.rooms.connection.ConnectionRoom;
import pd.levels.rooms.special.SpsShopRoom;
import pd.levels.rooms.special.SpsTentRoom;
import render.utils.math.Random;

import java.util.ArrayList;

/** Enforces the shop-and-tent topology used by SPS-PD transition floors. */
public class SpsBetweenBuilder extends FigureEightBuilder {

	@Override
	public ArrayList<Room> build(ArrayList<Room> rooms) {
		SpsShopRoom shop = null;
		SpsTentRoom tent = null;
		Room entrance = null;
		for (Room room : rooms) {
			if (room instanceof SpsShopRoom) shop = (SpsShopRoom)room;
			else if (room instanceof SpsTentRoom) tent = (SpsTentRoom)room;
			else if (room.isEntrance()) entrance = room;
		}
		if (shop == null || tent == null || entrance == null) return null;

		//SPSEXPD: 过渡层房间很少（商店 + 帐篷 + 极少数空房）时 8 字环无法成形，
		//改用紧凑布局：入口 → 商店 → 帐篷，其余房间挂在商店周围。
		if (rooms.size() <= 6) {
			return buildCompact(rooms, shop, tent, entrance);
		}

		// The legacy generator always chose the tent from rooms touching the shop.
		// Removing it from normal branch placement avoids relying on a low-probability
		// random branch choice (and RegularLevel retrying forever on unlucky seeds).
		rooms.remove(tent);
		setLandmarkRoom(shop);
		ArrayList<Room> result = super.build(rooms);
		if (result == null || area(shop) <= 54) return null;

		boolean tentPlaced = false;
		for (int tries = 0; tries < 32 && !tentPlaced; tries++) {
			tent.clearConnections();
			tentPlaced = placeRoom(result, shop, tent, Random.Float(360f)) != -1;
		}
		if (!tentPlaced || area(tent) <= 54) return null;
		result.add(tent);
		findNeighbours(result);
		if (!shop.connected.containsKey(tent) || tent.neigbours.contains(entrance)) return null;
		if (!fitsLegacyCanvas(result)) return null;

		for (Room room : result) {
			if (room != shop && !(room instanceof ConnectionRoom)
					&& !room.isEntrance() && !room.isExit()
					&& room.maxConnections(Room.ALL) > 1
					&& area(room) > area(shop)) return null;
		}
		return result;
	}

	/** 房间数很少时的紧凑过渡层布局（商店是枢纽，帐篷紧贴商店）。 */
	private ArrayList<Room> buildCompact(ArrayList<Room> rooms, SpsShopRoom shop,
			SpsTentRoom tent, Room entrance) {
		rooms.remove(tent);
		rooms.remove(shop);
		for (Room room : rooms) room.setEmpty();

		//以商店为锚点（与原 8 字布局一致：landmark 先行放置，其余房间围绕它）
		shop.setEmpty();
		if (!shop.setSize() || area(shop) <= 54) return null;
		shop.setPos(0, 0);

		ArrayList<Room> result = new ArrayList<>();
		result.add(shop);

		boolean entrancePlaced = false;
		for (int tries = 0; tries < 32 && !entrancePlaced; tries++) {
			entrance.clearConnections();
			entrancePlaced = placeRoom(result, shop, entrance, Random.Float(360f)) != -1;
		}
		if (!entrancePlaced) return null;
		result.add(entrance);

		boolean tentPlaced = false;
		for (int tries = 0; tries < 64 && !tentPlaced; tries++) {
			tent.clearConnections();
			if (placeRoom(result, shop, tent, Random.Float(360f)) == -1) continue;
			result.add(tent);
			findNeighbours(result);
			//帐篷不能贴着入口房（保持旧版过渡层的观感约束）
			if (tent.neigbours.contains(entrance)) {
				tent.clearConnections();
				result.remove(tent);
				continue;
			}
			tentPlaced = true;
		}
		if (!tentPlaced || area(tent) <= 54) return null;

		for (Room room : rooms) {
			if (room == entrance || room == shop || room == tent || result.contains(room)) continue;
			boolean placed = false;
			for (int tries = 0; tries < 32 && !placed; tries++) {
				room.clearConnections();
				placed = placeRoom(result, shop, room, Random.Float(360f)) != -1;
			}
			if (!placed) return null;
			result.add(room);
		}

		findNeighbours(result);
		if (!shop.connected.containsKey(tent) || tent.neigbours.contains(entrance)) return null;
		if (!fitsLegacyCanvas(result)) return null;
		return result;
	}

	private static int area(Room room) {
		return room.width() * room.height();
	}

	private static boolean fitsLegacyCanvas(ArrayList<Room> rooms) {
		int left = Integer.MAX_VALUE;
		int top = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int bottom = Integer.MIN_VALUE;
		for (Room room : rooms) {
			left = Math.min(left, room.left);
			top = Math.min(top, room.top);
			right = Math.max(right, room.right);
			bottom = Math.max(bottom, room.bottom);
		}
		// RegularPainter adds one border tile on each side and uses inclusive bounds.
		return right - left <= 45 && bottom - top <= 45;
	}
}
