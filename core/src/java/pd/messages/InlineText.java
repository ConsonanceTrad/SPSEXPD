/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */

package pd.messages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * SPSXPD: 中文原文内联注册表。
 *
 * <p>中文文本就近存放在使用它的类里（类内静态块调用 {@code InlineText.of(X.class).t("name", "…")}），
 * {@link Messages#get(Class, String, Object...)} 会优先查这里，命中即返回；未命中再走
 * {@code messages/<用途>/<语言>/*.properties}。</p>
 *
 * <p>key 生成规则与 {@link Messages} 完全一致，因此内联内容可以无缝替换 properties 中的中文条目，
 * 也可以随时清空本表回退到 properties 行为。</p>
 */
public final class InlineText {

	private static final Map<String, String> TABLE = new HashMap<>();
	private static final Set<String> LOADED = new HashSet<>();

	private InlineText() {
	}

	/**
	 * 完整 key，规则同 {@link Messages#get(Class, String, Object...)}：
	 * 类全限定名去掉 {@code pd.} 前缀 + {@code "." + k}，再整体转小写。
	 */
	public static String key(Class<?> owner, String k) {
		return (owner.getName().replace("pd.", "") + "." + k).toLowerCase(Locale.ENGLISH);
	}

	/** 查内联表（传入的 key 必须已是小写的完整 key）。 */
	public static String get(String fullKey) {
		return TABLE.get(fullKey);
	}

	public static boolean isEmpty() {
		return TABLE.isEmpty();
	}

	/** 返回该类的注册器。 */
	public static Builder of(Class<?> owner) {
		return new Builder(owner);
	}

	/**
	 * 强制初始化类，使其静态块执行、内联条目进入表中。
	 *
	 * <p>光用 {@code SomeClass.class} 字面量不会触发类初始化，所以当内联表查询未命中时，
	 * 需要在这里补一次初始化。每个类只尝试一次，失败静默忽略（回退到 properties）。</p>
	 */
	public static void ensureLoaded(Class<?> c) {
		if (c == null) {
			return;
		}
		String name = c.getName();
		if (!LOADED.add(name)) {
			return;
		}
		try {
			Class.forName(name, true, c.getClassLoader());
		} catch (Throwable ignored) {
			// 无内联条目或加载失败都属正常，交给 properties 兜底
		}
	}

	public static final class Builder {

		private final Class<?> owner;

		Builder(Class<?> owner) {
			this.owner = owner;
		}

		/** 注册一条中文原文。 */
		public Builder t(String k, String value) {
			TABLE.put(key(owner, k), value);
			return this;
		}
	}
}
