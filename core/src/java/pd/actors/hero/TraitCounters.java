/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 特质条件计数器 —— 供「满足条件即获得特质」使用（随英雄存档）。
 */

package pd.actors.hero;

import java.util.HashMap;

import render.utils.serialize.Bundle;

public class TraitCounters {

	/** 解除饥饿（吃满）次数 */
	public static final String HUNGER_RELIEVED = "hunger_relieved";
	/** 进食次数 */
	public static final String FOOD_EATEN = "food_eaten";
	/** 击杀数 */
	public static final String KILLS = "kills";
	/** 喝治疗药次数 */
	public static final String POTIONS_OF_HEALING = "poh_drunk";

	private static final String KEY = "sps_trait_counters";

	private final HashMap<String, Integer> counters = new HashMap<>();

	public int get(String key) {
		Integer v = counters.get(key);
		return v == null ? 0 : v;
	}

	public void set(String key, int value) {
		counters.put(key, value);
	}

	public int add(String key, int delta) {
		int v = get(key) + delta;
		counters.put(key, v);
		return v;
	}

	public void clear() {
		counters.clear();
	}

	public void storeInBundle(Bundle bundle) {
		Bundle b = new Bundle();
		for (java.util.Map.Entry<String, Integer> e : counters.entrySet()) {
			b.put(e.getKey(), e.getValue());
		}
		bundle.put(KEY, b);
	}

	public void restoreFromBundle(Bundle bundle) {
		counters.clear();
		if (!bundle.contains(KEY)) return;
		Bundle b = bundle.getBundle(KEY);
		for (String k : b.getKeys()) {
			counters.put(k, b.getInt(k));
		}
	}
}
