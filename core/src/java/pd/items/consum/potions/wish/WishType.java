/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * SPSEXPD: 许愿的类型词。
 *
 * 愿望里只写类型名（如「武器」「神器」「秘药」）时，从该类型的条目池里随机抽取一件。
 * 类型词优先级低于具体物品名（「治疗药剂」仍然是治疗药剂，不会被当成泛指"药剂"）。
 */
public final class WishType {

	public static final String WEAPON = "weapon";
	public static final String MISSILE = "missile";
	public static final String RANGED = "ranged";
	public static final String ARMOR = "armor";
	public static final String WAND = "wand";
	public static final String RING = "ring";
	public static final String ARTIFACT = "artifact";
	public static final String POTION = "potion";
	public static final String SCROLL = "scroll";
	public static final String STONE = "stone";
	public static final String SEED = "seed";
	public static final String FOOD = "food";
	public static final String MEDICINE = "medicine";
	public static final String BOMB = "bomb";
	public static final String BREW = "brew";
	public static final String ELIXIR = "elixir";
	public static final String SPELL = "spell";
	public static final String TRINKET = "trinket";
	/** 只有许愿才能得到的彩蛋物品。 */
	public static final String WISH_ONLY = "wish";

	/**
	 * 类型关键词 → 类型标签。
	 * 顺序即匹配顺序：更长的词必须排在更短的词前面（如「投掷武器」先于「武器」）。
	 */
	private static final Map<String, String> WORDS = new LinkedHashMap<>();

	static {
		//中文
		WORDS.put("投掷武器", MISSILE);
		WORDS.put("远程武器", RANGED);
		WORDS.put("秘药", ELIXIR);
		WORDS.put("魔药", BREW);
		WORDS.put("法术", SPELL);
		WORDS.put("结晶", SPELL);
		WORDS.put("武器", WEAPON);
		WORDS.put("护甲", ARMOR);
		WORDS.put("盔甲", ARMOR);
		WORDS.put("防具", ARMOR);
		WORDS.put("法杖", WAND);
		WORDS.put("魔杖", WAND);
		WORDS.put("戒指", RING);
		WORDS.put("指环", RING);
		WORDS.put("神器", ARTIFACT);
		WORDS.put("药剂", POTION);
		WORDS.put("药水", POTION);
		WORDS.put("卷轴", SCROLL);
		WORDS.put("符石", STONE);
		WORDS.put("种子", SEED);
		WORDS.put("食物", FOOD);
		WORDS.put("食品", FOOD);
		WORDS.put("药丸", MEDICINE);
		WORDS.put("药物", MEDICINE);
		WORDS.put("炸弹", BOMB);
		WORDS.put("饰品", TRINKET);
		//英文
		WORDS.put("missile", MISSILE);
		WORDS.put("ranged", RANGED);
		WORDS.put("elixir", ELIXIR);
		WORDS.put("brew", BREW);
		WORDS.put("spell", SPELL);
		WORDS.put("weapon", WEAPON);
		WORDS.put("armor", ARMOR);
		WORDS.put("armour", ARMOR);
		WORDS.put("wand", WAND);
		WORDS.put("ring", RING);
		WORDS.put("artifact", ARTIFACT);
		WORDS.put("potion", POTION);
		WORDS.put("scroll", SCROLL);
		WORDS.put("stone", STONE);
		WORDS.put("seed", SEED);
		WORDS.put("food", FOOD);
		WORDS.put("medicine", MEDICINE);
		WORDS.put("bomb", BOMB);
		WORDS.put("trinket", TRINKET);
	}

	private WishType() {
	}

	/** 愿望文本命中的类型标签；未命中返回 null。 */
	public static String match(String text) {
		if (text == null) return null;
		String lower = text.toLowerCase(Locale.ROOT);
		for (Map.Entry<String, String> word : WORDS.entrySet()) {
			if (lower.contains(word.getKey())) return word.getValue();
		}
		return null;
	}
}
