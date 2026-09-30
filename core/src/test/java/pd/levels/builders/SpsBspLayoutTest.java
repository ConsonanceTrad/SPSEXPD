package pd.levels.builders;

import render.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

/** Fixed-seed stress test for the SPS-PD 0.9.8 ordinary-floor BSP graph. */
public final class SpsBspLayoutTest {

	private static final int SEEDS = 5000;

	public static void main(String[] args) {
		long started = System.nanoTime();
		int totalAttempts = 0;
		for (int seed = 0; seed < SEEDS; seed++) {
			Random.pushGenerator(0x535053425350L + seed);
			try {
				SpsBspLayout.Result result = SpsBspLayout.generate(48, 48, 64);
				check(result != null, seed, "64次内未能生成布局");
				validate(result, seed);
				totalAttempts += result.attempts;
			} finally {
				Random.popGenerator();
			}
		}
		for (int seed = 0; seed < 250; seed++) {
			Random.pushGenerator(0x5448494546434154L + seed);
			try {
				SpsBspLayout.Result result = SpsBspLayout.generateThiefCatch(48, 48, 64);
				check(result != null, seed, "追捕关64次内未能生成布局");
				validateThiefCatch(result, seed);
			} finally {
				Random.popGenerator();
			}
		}
		long elapsedMs = (System.nanoTime() - started) / 1_000_000L;
		System.out.println("SPS普通层BSP压力测试通过：" + SEEDS + "个固定种子，构建尝试"
				+ totalAttempts + "次，耗时" + elapsedMs + "毫秒。");
	}

	private static void validateThiefCatch(SpsBspLayout.Result result, int seed) {
		check(result.entrance == result.exit, seed, "追捕关入口与返回房必须相同");
		check(result.entrance.top > 0 && result.entrance.top < 12, seed, "追捕关入口房不在地图上部");
		check(result.bossRoom != null, seed, "追捕关缺少独立王室");
		check(result.bossRoom.type == SpsBspLayout.Type.STANDARD, seed, "追捕关王室类型错误");
		check(result.bossRoom.connected.size() == 1, seed, "追捕关王室必须是环路外的独立末端");
		int standards = 0;
		for (SpsBspLayout.Room room : result.rooms) {
			if (room.type == SpsBspLayout.Type.STANDARD) standards++;
			if (room.bottom == result.entrance.top && room.type != SpsBspLayout.Type.NULL) {
				check(!room.neighbours.contains(result.entrance), seed, "入口房顶部存在已连接房间");
			}
		}
		check(standards == 5, seed, "追捕关必须有四个环路标准房和一个王室");

		HashSet<SpsBspLayout.Room> reached = new HashSet<>();
		ArrayList<SpsBspLayout.Room> pending = new ArrayList<>();
		pending.add(result.entrance);
		while (!pending.isEmpty()) {
			SpsBspLayout.Room room = pending.remove(pending.size() - 1);
			if (reached.add(room)) pending.addAll(room.connected.keySet());
		}
		check(reached.contains(result.bossRoom), seed, "追捕关入口无法到达王室");
		check(reached.containsAll(result.connected), seed, "追捕关存在孤立的已绘制房间");
	}

	private static void validate(SpsBspLayout.Result result, int seed) {
		check(result.rooms.size() >= 20, seed, "房间少于20个");
		check(result.entrance != result.exit, seed, "入口与出口是同一房间");
		check(result.connected.size() >= result.targetConnected, seed, "连通房间不足");
		for (SpsBspLayout.Room room : result.rooms) {
			check(room.left >= 0 && room.top >= 0 && room.right < 48 && room.bottom < 48,
					seed, "房间越出48x48地图");
			check(room.width() > 0 && room.height() > 0, seed, "房间尺寸无效");
			for (SpsBspLayout.Room neighbour : room.neighbours) {
				check(neighbour.neighbours.contains(room), seed, "相邻关系不是双向的");
			}
			for (SpsBspLayout.Room connected : room.connected.keySet()) {
				check(connected.connected.containsKey(room), seed, "连接关系不是双向的");
			}
		}

		HashSet<SpsBspLayout.Room> reached = new HashSet<>();
		ArrayList<SpsBspLayout.Room> pending = new ArrayList<>();
		pending.add(result.entrance);
		while (!pending.isEmpty()) {
			SpsBspLayout.Room room = pending.remove(pending.size() - 1);
			if (reached.add(room)) pending.addAll(room.connected.keySet());
		}
		check(reached.contains(result.exit), seed, "入口无法到达出口");
		check(reached.containsAll(result.connected), seed, "已连接集合中存在孤立房间");
	}

	private static void check(boolean condition, int seed, String message) {
		if (!condition) throw new AssertionError("种子" + seed + "：" + message);
	}

	private SpsBspLayoutTest() {
	}
}
