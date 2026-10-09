/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import java.util.Locale;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.messages.Messages;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 真护符的简化许愿。
 *
 * <p>与「许愿魔药」那套按幸运与描述准确度评分的判定不同，这里不做任何评分：
 * 愿望文本只要与许愿池里的物品名（精确或包含）对上，就直接给出该物品；
 * 对不上则视为听不懂，不消耗许愿次数。
 */
public final class SimplifiedWish {

	/** 一次简化许愿的结果。 */
	public enum Outcome {
		/** 命中并已投递物品 */
		GRANTED,
		/** 没有匹配到任何物品（不消耗次数） */
		NO_MATCH,
		/** 匹配到了但物品实例化/投递失败 */
		FAILED
	}

	private SimplifiedWish() { }

	/**
	 * 按名称直接给出一件许愿池里的物品。
	 *
	 * @param hero 许愿的英雄
	 * @param text 玩家写下的愿望文本
	 */
	public static Outcome grant(Hero hero, String text) {
		if (hero == null) return Outcome.FAILED;

		String wish = normalize(text);
		if (wish.isEmpty()) return Outcome.NO_MATCH;

		WishCatalog.Entry entry = bestMatch(wish);
		if (entry == null) return Outcome.NO_MATCH;

		Item item = create(entry.type);
		if (item == null) return Outcome.FAILED;

		deliver(hero, item);
		return Outcome.GRANTED;
	}

	/** 精确名优先，其次显示名与愿望文本互相包含。 */
	private static WishCatalog.Entry bestMatch(String wish) {
		WishCatalog.Entry contains = null;
		for (WishCatalog.Entry entry : WishCatalog.entries()) {
			String name = normalize(displayName(entry.type));
			if (name.isEmpty()) continue;
			if (name.equals(wish)) return entry;
			if (contains == null && (name.contains(wish) || wish.contains(name))) contains = entry;
		}
		return contains;
	}

	/** 物品显示名（简体走内联，其它语言走资源文件）；取不到返回 null。 */
	private static String displayName(Class<? extends Item> type) {
		try {
			String name = Messages.get(type, "name");
			if (name == null || name.equals(Messages.NO_TEXT_FOUND)) return null;
			return name;
		} catch (Throwable ignored) {
			return null;
		}
	}

	private static Item create(Class<? extends Item> type) {
		Item item;
		try {
			item = Reflection.newInstance(type);
		} catch (Throwable ignored) {
			return null;
		}
		if (item == null) return null;
		try {
			item.identify();
		} catch (Throwable ignored) {
			//测试等未初始化物品状态容器的环境下跳过鉴定
		}
		return item;
	}

	/** 交给英雄（背包放不下就掉在脚下）。 */
	private static void deliver(Hero hero, Item item) {
		boolean collected = false;
		try {
			collected = item.collect(hero.belongings.backpack);
		} catch (Throwable ignored) {
			collected = false;
		}
		if (collected) return;
		try {
			if (Dungeon.level != null) Dungeon.level.drop(item, hero.pos).sprite.drop(hero.pos);
		} catch (Throwable ignored) {
			//无关卡环境（测试）下不处理掉落
		}
	}

	private static String normalize(String text) {
		return text == null ? "" : text.trim().toLowerCase(Locale.ENGLISH);
	}
}
