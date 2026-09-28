package com.shatteredpixel.shatteredpixeldungeon.levels.builders;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpsShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpsTentRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ExitRoom;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

/** Deterministic stress test for the SPS transition-floor room topology. */
public final class SpsBetweenBuilderTest {

	private static final int[] DEPTHS = {1, 6, 11, 16, 21};
	private static final int SEEDS_PER_DEPTH = 500;

	public static void main(String[] args) {
		long started = System.nanoTime();
		int attempts = 0;
		for (int depth : DEPTHS) {
			Dungeon.depth = depth;
			for (int seed = 0; seed < SEEDS_PER_DEPTH; seed++) {
				Random.pushGenerator(0x535053L * depth + seed);
				try {
					ArrayList<Room> built = null;
					int retries = 0;
					while (built == null && retries++ < 100) {
						built = new SpsBetweenBuilder().build(rooms());
					}
					check(built != null, depth, seed, "100次内未能生成地图");
					validate(built, depth, seed);
					attempts += retries;
				} finally {
					Random.popGenerator();
				}
			}
		}
		long elapsedMs = (System.nanoTime() - started) / 1_000_000L;
		System.out.println("SPS过渡层压力测试通过：2500个固定种子，构建尝试"
				+ attempts + "次，耗时" + elapsedMs + "毫秒。");
	}

	private static ArrayList<Room> rooms() {
		ArrayList<Room> rooms = new ArrayList<>();
		rooms.add(new EntranceRoom());
		rooms.add(new ExitRoom());
		for (int i = 0; i < 8; i++) rooms.add(new EmptyRoom());
		rooms.add(new SpsShopRoom());
		rooms.add(new SpsTentRoom());
		Random.shuffle(rooms);
		return rooms;
	}

	private static void validate(ArrayList<Room> rooms, int depth, int seed) {
		SpsShopRoom shop = null;
		SpsTentRoom tent = null;
		Room entrance = null;
		for (Room room : rooms) {
			if (room instanceof SpsShopRoom) shop = (SpsShopRoom)room;
			else if (room instanceof SpsTentRoom) tent = (SpsTentRoom)room;
			else if (room.isEntrance()) entrance = room;
		}
		check(shop != null && tent != null && entrance != null, depth, seed, "缺少关键房间");
		check(shop.connected.containsKey(tent), depth, seed, "帐篷没有直接连接商店");
		check(!tent.neigbours.contains(entrance), depth, seed, "帐篷贴近入口房");
		check(shop.width() * shop.height() > 54, depth, seed, "商店面积不足");
		check(tent.width() * tent.height() > 54, depth, seed, "帐篷面积不足");
		HashSet<Room> reachable = new HashSet<>();
		ArrayList<Room> pending = new ArrayList<>();
		pending.add(entrance);
		while (!pending.isEmpty()) {
			Room room = pending.remove(pending.size() - 1);
			if (reachable.add(room)) pending.addAll(room.connected.keySet());
		}
		check(reachable.size() == rooms.size(), depth, seed, "存在不可达房间");
		for (Room room : rooms) {
			for (Room connected : room.connected.keySet()) {
				check(connected.connected.containsKey(room), depth, seed, "房间连接不是双向的");
			}
		}
	}

	private static void check(boolean condition, int depth, int seed, String message) {
		if (!condition) {
			throw new AssertionError("深度" + depth + "，种子" + seed + "：" + message);
		}
	}

	private SpsBetweenBuilderTest() {
	}
}
