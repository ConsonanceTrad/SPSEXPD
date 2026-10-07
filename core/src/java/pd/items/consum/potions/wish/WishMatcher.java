/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * SPSEXPD: 许愿词匹配。
 *
 * 愿望文本分词后与候选物品的"名称 + 描述"比对：
 * 名称命中权重 1.0、描述命中权重 0.5，累计命中数换算成 0~1 的"描述准确度"。
 */
public final class WishMatcher {

	/** 低于该准确度视为没有命中任何物品（相当于至少命中一个名称词）。 */
	public static final double MIN_ACCURACY = 1d;

	/** 各级命中的权重：2 字以上的名称词 > 单个名称字 > 描述词。 */
	private static final double GRAM_WEIGHT = 1d;
	private static final double SINGLE_WEIGHT = 0.5d;
	private static final double DESC_WEIGHT = 0.1d;

	private WishMatcher() {
	}

	/** 分词：英文/数字取 2 字符以上的连续段，中日文取 2-gram（单字也保留）。 */
	public static List<String> tokenize(String text) {
		List<String> tokens = new ArrayList<>();
		if (text == null) return tokens;
		String lower = text.toLowerCase(Locale.ROOT);

		StringBuilder ascii = new StringBuilder();
		for (int i = 0; i < lower.length(); i++) {
			char c = lower.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
				ascii.append(c);
			} else {
				if (ascii.length() >= 2) tokens.add(ascii.toString());
				ascii.setLength(0);
			}
		}
		if (ascii.length() >= 2) tokens.add(ascii.toString());

		StringBuilder cjk = new StringBuilder();
		for (int i = 0; i < lower.length(); i++) {
			char c = lower.charAt(i);
			boolean isCjk = (c >= '\u4e00' && c <= '\u9fff') || (c >= '\u3040' && c <= '\u30ff');
			cjk.append(isCjk ? c : ' ');
		}
		for (String run : cjk.toString().split("\\s+")) {
			if (run.isEmpty()) continue;
			for (int i = 0; i < run.length(); i++) {
				tokens.add(run.substring(i, i + 1));
				if (i + 2 <= run.length()) tokens.add(run.substring(i, i + 2));
			}
		}
		return tokens;
	}

	/**
	 * 愿望与某条目的描述准确度（0~1）。
	 *
	 * 许愿是<b>按名称做词匹配</b>的：必须命中名称——一个 2 字以上的词，
	 * 或两个以上的单字——才算命中该物品；命中的描述词只做小幅加权。
	 * 因此准确度要么是 0（没对上一个名字），要么不低于 {@link #MIN_ACCURACY}。
	 */
	public static double accuracy(String text, WishCatalog.Entry entry) {
		if (entry == null) return 0d;
		return accuracy(text, entry.name, entry.desc);
	}

	/** 名称 + 描述的通用准确度（怪物名称匹配也走同一套分词规则）。 */
	public static double accuracy(String text, String name, String desc) {
		if (name == null) return 0d;
		String target = name;
		String body = desc == null ? "" : desc;
		int grams = 0;
		int singles = 0;
		int descHits = 0;
		for (String token : tokenize(text)) {
			if (target.contains(token)) {
				if (token.length() >= 2) grams++;
				else singles++;
			} else if (!body.isEmpty() && body.contains(token)) {
				descHits++;
			}
		}
		if (grams < 1 && singles < 2) return 0d;
		return grams * GRAM_WEIGHT + singles * SINGLE_WEIGHT + descHits * DESC_WEIGHT;
	}

	/** 在候选池中取准确度最高的条目；并列时取等级更低、名字更短者。 */
	public static WishCatalog.Entry best(String text, List<WishCatalog.Entry> pool) {
		WishCatalog.Entry best = null;
		double bestAccuracy = 0d;
		for (WishCatalog.Entry entry : pool) {
			double accuracy = accuracy(text, entry);
			if (accuracy <= 0d) continue;
			if (best == null
					|| accuracy > bestAccuracy
					|| (accuracy == bestAccuracy && better(entry, best))) {
				best = entry;
				bestAccuracy = accuracy;
			}
		}
		return best;
	}

	/** 在指定等级的条目中取最贴近愿望的一个。 */
	public static WishCatalog.Entry bestOfTier(String text, List<WishCatalog.Entry> pool, int tier) {
		List<WishCatalog.Entry> tiered = new ArrayList<>();
		for (WishCatalog.Entry entry : pool) {
			if (entry.tier == tier) tiered.add(entry);
		}
		return best(text, tiered);
	}

	private static boolean better(WishCatalog.Entry candidate, WishCatalog.Entry current) {
		if (candidate.tier != current.tier) return candidate.tier < current.tier;
		return candidate.name.length() < current.name.length();
	}
}
