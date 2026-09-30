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
import com.watabou.utils.Random;

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
