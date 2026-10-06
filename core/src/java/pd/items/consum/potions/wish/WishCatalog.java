/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.items.Generator;
import pd.items.Generator.Category;
import pd.items.Item;
import pd.items.Recipe;
import pd.messages.Messages;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * SPSEXPD: 许愿候选池。
 *
 * 候选池取自生成器类别中"适合作为奖励"的物品，并统一排除：
 * <ul>
 *   <li>任务/剧情/图鉴/钥匙/角色专属物品（复用 {@link Recipe#isSpecialItem}）；</li>
 *   <li>调试物品（类名含 Test 的测试装备、测试消耗品等）；</li>
 *   <li>货币、宠物蛋、召唤物等不适合作为许愿奖励的类别。</li>
 * </ul>
 * 每个条目带有奖励等级（tier 1~4），幸运值门槛由 {@link WishRewardTable} 决定。
 */
public final class WishCatalog {

	/** 一个可许愿的物品条目。 */
	public static final class Entry {
		public final Class<? extends Item> type;
		public final int tier;
		public final String name;
		public final String desc;
		public final boolean wishOnly;

		public Entry(Class<? extends Item> type, int tier, String name, String desc, boolean wishOnly) {
			this.type = type;
			this.tier = tier;
			this.name = name;
			this.desc = desc == null ? "" : desc;
			this.wishOnly = wishOnly;
		}
	}

	/** 类别 → 奖励等级。未列出的类别不参与许愿。 */
	private static final Map<Category, Integer> TIERS = new EnumMap<>(Category.class);

	static {
		TIERS.put(Category.WEP_T1, 1);
		TIERS.put(Category.WEP_T2, 2);
		TIERS.put(Category.WEP_T3, 3);
		TIERS.put(Category.WEP_T4, 4);
		TIERS.put(Category.WEP_T5, 4);
		TIERS.put(Category.MIS_T1, 1);
		TIERS.put(Category.MIS_T2, 2);
		TIERS.put(Category.MIS_T3, 3);
		TIERS.put(Category.MIS_T4, 4);
		TIERS.put(Category.MIS_T5, 4);
		TIERS.put(Category.MISSILE, 2);
		TIERS.put(Category.ARROWS, 1);
		TIERS.put(Category.RANGED, 3);
		TIERS.put(Category.RANGEWEAPON, 3);
		TIERS.put(Category.GUNWEAPON, 3);
		TIERS.put(Category.MUSICWEAPON, 3);
		TIERS.put(Category.ARMOR, 3);
		TIERS.put(Category.WAND, 3);
		TIERS.put(Category.RING, 3);
		TIERS.put(Category.ARTIFACT, 4);
		TIERS.put(Category.TRINKET, 2);
		TIERS.put(Category.FOOD, 1);
		TIERS.put(Category.HIGHFOOD, 2);
		TIERS.put(Category.POTION, 2);
		TIERS.put(Category.SCROLL, 2);
		TIERS.put(Category.STONE, 2);
		TIERS.put(Category.SEED, 1);
		TIERS.put(Category.MEDICINE, 2);
		TIERS.put(Category.MUSHROOM, 2);
		TIERS.put(Category.PILL, 2);
	}

	private static List<Entry> entries;

	private WishCatalog() {
	}

	/** 可许愿物品的完整列表（只读）。 */
	public static synchronized List<Entry> entries() {
		if (entries == null) entries = build();
		return entries;
	}

	/** 明确不可许愿的类（货币与调试/生成工具）。 */
	private static final Set<Class<?>> NEVER = new LinkedHashSet<>();

	static {
		NEVER.add(pd.items.Gold.class);
		NEVER.add(pd.items.Garbage.class);
	}

	private static List<Entry> build() {
		LinkedHashMap<Class<? extends Item>, Integer> tiers = new LinkedHashMap<>();

		for (Generator.Category category : Generator.Category.values()) {
			Integer tier = TIERS.get(category);
			if (tier == null || category.classes == null) continue;
			for (Class<?> raw : category.classes) {
				if (!Item.class.isAssignableFrom(raw)) continue;
				@SuppressWarnings("unchecked")
				Class<? extends Item> type = (Class<? extends Item>) raw;
				Integer current = tiers.get(type);
				if (current == null || tier < current) tiers.put(type, tier);
			}
		}

		List<Entry> result = new ArrayList<>();
		for (Map.Entry<Class<? extends Item>, Integer> candidate : tiers.entrySet()) {
			Entry entry = entryOf(candidate.getKey(), candidate.getValue(), false);
			if (entry != null) result.add(entry);
		}

		//SPSEXPD: 只有许愿才能获得的彩蛋物品
		for (Class<? extends Item> type : WishOnlyItem.types()) {
			Entry entry = entryOf(type, WishOnlyItem.TIER, true);
			if (entry != null) result.add(entry);
		}

		return Collections.unmodifiableList(result);
	}

	private static Entry entryOf(Class<? extends Item> type, int tier, boolean wishOnly) {
		if (excluded(type)) return null;
		String fallback = fallbackName(type);
		String display = text(type, "name");
		//名称资源可用时以显示名为主，同时保留类名兜底（多语言或资源缺失时仍可匹配）
		String matchName = display == null ? fallback : display.toLowerCase(Locale.ENGLISH) + " " + fallback;
		return new Entry(type, tier, matchName, text(type, "desc"), wishOnly);
	}

	/** 名称资源不可用时的兜底匹配名（取自类名，内部类以空格连接）。 */
	private static String fallbackName(Class<?> type) {
		String fqn = type.getName();
		int dot = fqn.lastIndexOf('.');
		String simple = dot < 0 ? fqn : fqn.substring(dot + 1);
		return simple.replace('$', ' ').toLowerCase(Locale.ENGLISH);
	}

	/** 是否被排除在许愿范围之外。 */
	public static boolean excluded(Class<? extends Item> type) {
		if (NEVER.contains(type)) return true;
		//SPSEXPD: 调试物品（测试装备/测试消耗品等）不提供给许愿
		if (type.getSimpleName().startsWith("Test")) return true;
		try {
			Item instance = Reflection.newInstance(type);
			if (instance != null && Recipe.isSpecialItem(instance)) return true;
		} catch (Throwable ignored) {
			//无法实例化的类别（抽象类等）直接排除
			return true;
		}
		return false;
	}

	private static String text(Class<? extends Item> type, String key) {
		try {
			String value = Messages.get(type, key);
			if (value == null || value.equals(Messages.NO_TEXT_FOUND) || value.isEmpty()) return null;
			return value;
		} catch (Throwable ignored) {
			return null;
		}
	}
}
