/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.items.Generator;
import pd.items.Generator.Category;
import pd.items.Item;
import pd.items.Recipe;
import pd.items.consum.potions.brews.Brew;
import pd.items.consum.potions.elixirs.Elixir;
import pd.journal.Catalog;
import pd.messages.Messages;
import render.utils.math.Random;
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
 * 候选池取自生成器类别与图鉴的魔药/秘药、法术、炸弹分组，并统一排除：
 * <ul>
 *   <li>任务/剧情/图鉴/钥匙/角色专属物品（复用 {@link Recipe#isSpecialItem}）；</li>
 *   <li>调试物品（类名含 Test 的测试装备、测试消耗品等）；</li>
 *   <li>货币、宠物蛋、召唤物等不适合作为许愿奖励的类别。</li>
 * </ul>
 * 每个条目带有奖励等级（tier 1~4）与类型标签（见 {@link WishType}）：
 * 幸运值门槛由 {@link WishRewardTable} 决定，类型标签用于「武器/神器/秘药」这类类型词许愿。
 */
public final class WishCatalog {

	/** 一个可许愿的物品条目。 */
	public static final class Entry {
		public final Class<? extends Item> type;
		public final int tier;
		public final String name;
		public final String desc;
		public final boolean wishOnly;
		/** SPSEXPD: 类型标签（见 WishType）；可能为 null（未分类） */
		public final String kind;

		public Entry(Class<? extends Item> type, int tier, String name, String desc, boolean wishOnly) {
			this(type, tier, name, desc, wishOnly, null);
		}

		public Entry(Class<? extends Item> type, int tier, String name, String desc,
					 boolean wishOnly, String kind) {
			this.type = type;
			this.tier = tier;
			this.name = name;
			this.desc = desc == null ? "" : desc;
			this.wishOnly = wishOnly;
			this.kind = kind;
		}
	}

	/** 类别 → 奖励等级。未列出的类别不参与许愿。 */
	private static final Map<Category, Integer> TIERS = new EnumMap<>(Category.class);

	/** 类别 → 类型标签。 */
	private static final Map<Category, String> KINDS = new EnumMap<>(Category.class);

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

		KINDS.put(Category.WEP_T1, WishType.WEAPON);
		KINDS.put(Category.WEP_T2, WishType.WEAPON);
		KINDS.put(Category.WEP_T3, WishType.WEAPON);
		KINDS.put(Category.WEP_T4, WishType.WEAPON);
		KINDS.put(Category.WEP_T5, WishType.WEAPON);
		KINDS.put(Category.MIS_T1, WishType.MISSILE);
		KINDS.put(Category.MIS_T2, WishType.MISSILE);
		KINDS.put(Category.MIS_T3, WishType.MISSILE);
		KINDS.put(Category.MIS_T4, WishType.MISSILE);
		KINDS.put(Category.MIS_T5, WishType.MISSILE);
		KINDS.put(Category.MISSILE, WishType.MISSILE);
		KINDS.put(Category.ARROWS, WishType.MISSILE);
		KINDS.put(Category.RANGED, WishType.RANGED);
		KINDS.put(Category.RANGEWEAPON, WishType.RANGED);
		KINDS.put(Category.GUNWEAPON, WishType.RANGED);
		KINDS.put(Category.MUSICWEAPON, WishType.RANGED);
		KINDS.put(Category.ARMOR, WishType.ARMOR);
		KINDS.put(Category.WAND, WishType.WAND);
		KINDS.put(Category.RING, WishType.RING);
		KINDS.put(Category.ARTIFACT, WishType.ARTIFACT);
		KINDS.put(Category.TRINKET, WishType.TRINKET);
		KINDS.put(Category.FOOD, WishType.FOOD);
		KINDS.put(Category.HIGHFOOD, WishType.FOOD);
		KINDS.put(Category.POTION, WishType.POTION);
		KINDS.put(Category.SCROLL, WishType.SCROLL);
		KINDS.put(Category.STONE, WishType.STONE);
		KINDS.put(Category.SEED, WishType.SEED);
		KINDS.put(Category.MEDICINE, WishType.MEDICINE);
		KINDS.put(Category.MUSHROOM, WishType.MEDICINE);
		KINDS.put(Category.PILL, WishType.MEDICINE);
	}

	private static List<Entry> entries;

	private WishCatalog() {
	}

	/** 可许愿物品的完整列表（只读）。 */
	public static synchronized List<Entry> entries() {
		if (entries == null) entries = build();
		return entries;
	}

	/** 指定类型标签下的全部条目。 */
	public static List<Entry> ofKind(String kind) {
		List<Entry> result = new ArrayList<>();
		if (kind == null) return result;
		for (Entry entry : entries()) {
			if (kind.equals(entry.kind)) result.add(entry);
		}
		return result;
	}

	/** 从指定类型标签的条目里随机抽一件；该类型为空时返回 null。 */
	public static Entry randomOfKind(String kind) {
		List<Entry> pool = ofKind(kind);
		return pool.isEmpty() ? null : Random.element(pool);
	}

	/** 明确不可许愿的类（货币与调试/生成工具）。 */
	private static final Set<Class<?>> NEVER = new LinkedHashSet<>();

	static {
		NEVER.add(pd.items.Gold.class);
		NEVER.add(pd.items.Garbage.class);
	}

	private static List<Entry> build() {
		LinkedHashMap<Class<? extends Item>, Integer> tiers = new LinkedHashMap<>();
		LinkedHashMap<Class<? extends Item>, String> kinds = new LinkedHashMap<>();

		for (Generator.Category category : Generator.Category.values()) {
			Integer tier = TIERS.get(category);
			if (tier == null || category.classes == null) continue;
			String kind = KINDS.get(category);
			for (Class<?> raw : category.classes) {
				if (!Item.class.isAssignableFrom(raw)) continue;
				@SuppressWarnings("unchecked")
				Class<? extends Item> type = (Class<? extends Item>) raw;
				Integer current = tiers.get(type);
				if (current == null || tier < current) {
					tiers.put(type, tier);
					kinds.put(type, kind);
				}
			}
		}

		//SPSEXPD: 魔药/秘药、法术结晶、炸弹不在生成器类别里，从图鉴分组补入
		addCatalogGroup(tiers, kinds, Catalog.BREWS_ELIXIRS, null);
		addCatalogGroup(tiers, kinds, Catalog.SPELLS, WishType.SPELL);
		addCatalogGroup(tiers, kinds, Catalog.BOMBS, WishType.BOMB);

		List<Entry> result = new ArrayList<>();
		for (Map.Entry<Class<? extends Item>, Integer> candidate : tiers.entrySet()) {
			Class<? extends Item> type = candidate.getKey();
			Entry entry = entryOf(type, candidate.getValue(), kinds.get(type), false);
			if (entry != null) result.add(entry);
		}

		//SPSEXPD: 只有许愿才能获得的彩蛋物品
		for (Class<? extends Item> type : WishOnlyItem.types()) {
			Entry entry = entryOf(type, WishOnlyItem.TIER, WishType.WISH_ONLY, true);
			if (entry != null) result.add(entry);
		}

		return Collections.unmodifiableList(result);
	}

	/** 从图鉴分组补入候选池；kind 为 null 时按魔药/秘药父类细分。 */
	private static void addCatalogGroup(LinkedHashMap<Class<? extends Item>, Integer> tiers,
										LinkedHashMap<Class<? extends Item>, String> kinds,
										Catalog catalog, String kind) {
		int tier = catalog == Catalog.BREWS_ELIXIRS ? 4 : catalog == Catalog.SPELLS ? 3 : 2;
		for (Class<?> raw : catalog.items()) {
			if (!Item.class.isAssignableFrom(raw)) continue;
			@SuppressWarnings("unchecked")
			Class<? extends Item> type = (Class<? extends Item>) raw;
			if (tiers.containsKey(type)) continue;
			tiers.put(type, tier);
			kinds.put(type, kind != null ? kind : brewOrElixir(type));
		}
	}

	/** 魔药（Brew）与秘药（Elixir）共用一个图鉴分组，按父类细分。 */
	private static String brewOrElixir(Class<? extends Item> type) {
		if (Brew.class.isAssignableFrom(type)) return WishType.BREW;
		return WishType.ELIXIR;
	}

	private static Entry entryOf(Class<? extends Item> type, int tier, String kind, boolean wishOnly) {
		if (excluded(type)) return null;
		String fallback = fallbackName(type);
		String display = text(type, "name");
		//名称资源可用时以显示名为主，同时保留类名兜底（多语言或资源缺失时仍可匹配）
		String matchName = display == null ? fallback : display.toLowerCase(Locale.ENGLISH) + " " + fallback;
		return new Entry(type, tier, matchName, text(type, "desc"), wishOnly, kind);
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
