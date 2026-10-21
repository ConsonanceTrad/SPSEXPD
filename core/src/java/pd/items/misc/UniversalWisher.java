/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import java.util.ArrayList;
import java.util.List;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.consum.potions.wish.WishCatalog;
import pd.items.consum.potions.wish.WishMatcher;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndTextInput;
import render.utils.serialize.Reflection;

/**
 * SPSEXPD: 万能许愿器（测试时间专属道具）。
 *
 * <p>与真正的许愿（{@code WishPotion} → {@code WishEngine}）完全分开：
 * 不吃幸运、不吃描述准确度加成，**只看名字**——
 * 写下物品名就直接给你名字最接近的东西，并列的候选一并给予（最多 3 项，
 * 免得「药剂」「武器」这种宽泛词一次塞满背包）。
 *
 * <p>可以在名字后面加数量（{@code 治疗药剂 x20} / {@code 治疗药剂*20} / {@code 治疗药剂 20}），
 * 数量**只对可堆叠物品生效**，不可堆叠物品一律 1 件。使用不消耗、无限次。
 */
public class UniversalWisher extends Item {

	public static final String AC_USE = "USE";

	/** 输入框最大字符数（与许愿魔药一致）。 */
	public static final int MAX_INPUT_LENGTH = 40;
	/** 并列候选最多给予几项。 */
	public static final int MAX_CANDIDATES = 3;
	/** 单次可获得的最大数量（仅对可堆叠物品）。 */
	public static final int MAX_QUANTITY = 999;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(UniversalWisher.class)
			.t("name", "万能许愿器")
			.t("desc", "测试用道具。写下物品名，它就把名字最接近的东西直接给你——"
					+ "并列的候选会一并给予（最多 3 项）。名字后面可以加数量，"
					+ "例如「治疗药剂 x20」，数量只对可堆叠物品生效。"
					+ "它不消耗，也用不完。")
			.t("ac_use", "许愿")
			.t("title", "万能许愿器")
			.t("body", "写下物品名：\n"
					+ "· 名字最接近的候选一并给予，最多 3 项\n"
					+ "· 可加数量：治疗药剂 x20（只对可堆叠物品生效）\n"
					+ "· 不消耗，无限次使用")
			.t("ok", "许愿")
			.t("cancel", "取消")
			.t("no_match", "没有找到名字接近的物品。")
			.t("granted", "你获得了：%s");
	}

	{
		image = SpecificPlaceHolderDict.ELIXIR_HOLDER_0;
		unique = true;
		bones = false;
		defaultAction = AC_USE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_USE.equals(action)) return;
		curUser = hero;
		open(hero);
	}

	/** 弹出输入窗（不消耗物品，可反复使用）。 */
	public static void open(final Hero hero) {
		if (hero == null) return;
		GameScene.show(new WndTextInput(
				Messages.get(UniversalWisher.class, "title"),
				Messages.get(UniversalWisher.class, "body"),
				"",
				MAX_INPUT_LENGTH,
				false,
				Messages.get(UniversalWisher.class, "ok"),
				Messages.get(UniversalWisher.class, "cancel")) {
			@Override
			public void onSelect(boolean positive, String text) {
				if (positive) grant(hero, text);
			}
		});
	}

	/** 按名字发放：最高分候选（最多 3 项）全部给予。 */
	public static void grant(Hero hero, String rawText) {
		if (hero == null || rawText == null) return;
		String text = rawText.trim();
		if (text.isEmpty()) return;

		String[] parts = splitQuantity(text);
		List<WishCatalog.Entry> matches =
				WishMatcher.topMatches(parts[0], WishCatalog.entries(), MAX_CANDIDATES);
		if (matches.isEmpty()) {
			GLog.w(Messages.get(UniversalWisher.class, "no_match"));
			return;
		}

		int quantity = parseQuantity(parts[1]);
		StringBuilder granted = new StringBuilder();
		for (WishCatalog.Entry entry : matches) {
			Item item = Reflection.newInstance(entry.type);
			if (item == null) continue;
			item.identify();
			int amount = item.stackable && quantity > 1 ? quantity : 1;
			item.quantity(amount);
			if (!item.collect()) Dungeon.level.drop(item, hero.pos);

			if (granted.length() > 0) granted.append("、");
			granted.append(Messages.titleCase(item.name()));
			if (amount > 1) granted.append(" ×").append(amount);
		}
		if (granted.length() > 0) {
			GLog.p(Messages.get(UniversalWisher.class, "granted", granted.toString()));
		}
	}

	/**
	 * 把「名字 xN」拆成 {名字, 数量文本}。没有尾部数字时数量文本为 "1"。
	 */
	public static String[] splitQuantity(String text) {
		String trimmed = text.trim();
		int digitsStart = -1;
		for (int i = trimmed.length() - 1; i >= 0; i--) {
			char c = trimmed.charAt(i);
			if (Character.isDigit(c)) {
				digitsStart = i;
				continue;
			}
			break;
		}
		//整串都是数字、或以数字开头（没有名字部分）→ 不当作数量
		if (digitsStart <= 0) return new String[]{trimmed, "1"};

		String digits = trimmed.substring(digitsStart);
		String name = trimmed.substring(0, digitsStart).trim();
		while (!name.isEmpty() && "xX×*".indexOf(name.charAt(name.length() - 1)) >= 0) {
			name = name.substring(0, name.length() - 1).trim();
		}
		if (name.isEmpty()) return new String[]{trimmed, "1"};
		return new String[]{name, digits};
	}

	/** 解析数量并夹到 1~{@link #MAX_QUANTITY}。 */
	public static int parseQuantity(String digits) {
		try {
			return Math.max(1, Math.min(MAX_QUANTITY, Integer.parseInt(digits.trim())));
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 0;
	}
}
