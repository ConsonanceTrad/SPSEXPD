/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.mobs.Mob;
import pd.actors.mobs.MobSpawner;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;

import java.util.ArrayList;
import java.util.Locale;

/**
 * SPSEXPD: 许愿召唤怪物。
 *
 * 怪物愿望只会从**当前层的普通怪轮转表**（{@link MobSpawner#getMobRotation(int)}）里取，
 * 因此不会召出 boss、NPC 或剧情专属怪物；召唤位置复用英雄身旁的空格查找。
 */
public final class WishSummon {

	/** 泛指"怪物"的关键词。 */
	private static final String[] GENERIC = {
			"怪物", "敌人", "魔物", "妖怪", "生物", "mob", "monster", "enemy"
	};

	private WishSummon() {
	}

	/** 当前层可以正常刷出的普通怪物。 */
	public static ArrayList<Class<? extends Mob>> rotation() {
		ArrayList<Class<? extends Mob>> result = new ArrayList<>();
		try {
			if (Dungeon.depth <= 0) return result;
			ArrayList<Class<? extends Mob>> rotation = MobSpawner.getMobRotation(Dungeon.depth);
			if (rotation != null) result.addAll(rotation);
		} catch (Throwable ignored) {
			//关卡/深度不可用时视为没有怪物池
		}
		return result;
	}

	/** 愿望是不是泛指"怪物"。 */
	public static boolean isGeneric(String text) {
		if (text == null) return false;
		String lower = text.toLowerCase(Locale.ROOT);
		for (String word : GENERIC) {
			if (lower.contains(word)) return true;
		}
		return false;
	}

	/** 在给定怪物池里按名称匹配最贴近的一只；没有匹配返回 null。 */
	public static Class<? extends Mob> matchNamed(String text, ArrayList<Class<? extends Mob>> pool) {
		if (text == null || pool.isEmpty()) return null;
		Class<? extends Mob> best = null;
		double bestScore = 0d;
		for (Class<? extends Mob> type : pool) {
			String display = nameOf(type);
			String fallback = fallbackName(type);
			String matchName = display == null
					? fallback : display.toLowerCase(Locale.ENGLISH) + " " + fallback;
			double score = WishMatcher.accuracy(text, matchName, "");
			if (score <= 0d) continue;
			if (best == null || score > bestScore) {
				best = type;
				bestScore = score;
			}
		}
		return best;
	}

	/** 在英雄身旁的空格召唤一只怪物；没有位置或生成失败返回 null。 */
	public static Mob summon(Class<? extends Mob> type) {
		if (type == null || Dungeon.level == null || Dungeon.hero == null) return null;
		try {
			int cell = findSpawnCell();
			if (cell < 0) return null;
			Mob mob = type.getDeclaredConstructor().newInstance();
			mob.pos = cell;
			GameScene.add(mob);
			return mob;
		} catch (Throwable ignored) {
			return null;
		}
	}

	/** 怪物显示名（资源缺失时返回 null）。 */
	public static String nameOf(Class<? extends Mob> type) {
		if (type == null) return null;
		try {
			Mob mob = type.getDeclaredConstructor().newInstance();
			String name = mob.name();
			return name == null || name.isEmpty() ? null : name;
		} catch (Throwable ignored) {
			return null;
		}
	}

	/** 英雄周围的空格：相邻 8 格优先，半径 2 兜底；找不到返回 -1。 */
	public static int findSpawnCell() {
		if (Dungeon.hero == null || Dungeon.level == null) return -1;
		int hero = Dungeon.hero.pos;
		int width = Dungeon.level.width();

		ArrayList<Integer> ring1 = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int c = hero + offset;
			if (c < 0 || c >= Dungeon.level.length()) continue;
			if (!Dungeon.level.passable[c] || Actor.findChar(c) != null) continue;
			ring1.add(c);
		}
		if (!ring1.isEmpty()) return ring1.get(0);

		for (int y = -2; y <= 2; y++) {
			for (int x = -2; x <= 2; x++) {
				if (Math.abs(x) < 2 && Math.abs(y) < 2) continue;
				int c = hero + x + y * width;
				if (c < 0 || c >= Dungeon.level.length()) continue;
				if (!Dungeon.level.passable[c] || Actor.findChar(c) != null) continue;
				return c;
			}
		}
		return -1;
	}

	private static String fallbackName(Class<?> type) {
		String fqn = type.getName();
		int dot = fqn.lastIndexOf('.');
		String simple = dot < 0 ? fqn : fqn.substring(dot + 1);
		return simple.replace('$', ' ').toLowerCase(Locale.ENGLISH);
	}
}
