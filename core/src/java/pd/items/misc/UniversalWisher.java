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
					+ "独特物品、剧情与角色专属物品也在它的范围内。它不消耗，也用不完。")
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
				WishMatcher.topMatches(parts[0], matchPool(), MAX_CANDIDATES);
		if (matches.isEmpty()) {
			GLog.w(Messages.get(UniversalWisher.class, "no_match"));
			return;
		}

		int quantity = parseQuantity(parts[1]);
		StringBuilder granted = new StringBuilder();
		for (WishCatalog.Entry entry : matches) {
			Item item;
			try {
				item = Reflection.newInstance(entry.type);
			} catch (Throwable t) {
				item = null;
			}
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

	//*** 匹配池

	/** 匹配池缓存（全物品类，含独特物品与特殊物品）。 */
	private static List<WishCatalog.Entry> matchPool;

	/**
	 * 万能许愿器的匹配池：**全物品类**。
	 *
	 * <p>与真许愿的 {@link WishCatalog#entries()} 不同，这里不受
	 * {@code WishCatalog.excluded()} 的限制——独特物品（`UniqueItem`）、
	 * 任务/剧情/角色专属等「特殊物品」也能被许出来（用户裁决）。
	 * 只跳过货币与测试装备，避免无意义的结果。
	 *
	 * <p>数据来源：许愿池已有的条目直接复用（名字/描述已按当前语言处理好），
	 * 其余从调试器的全物品类全集（{@code WndDebugItems.allItems()}）补入，
	 * 匹配名用「显示名 + 类名」两者，中英文都能对上。
	 */
	private static List<WishCatalog.Entry> matchPool() {
		if (matchPool != null) return matchPool;

		List<WishCatalog.Entry> pool = new ArrayList<>();
		java.util.Set<Class<? extends Item>> seen = new java.util.HashSet<>();
		for (WishCatalog.Entry entry : WishCatalog.entries()) {
			pool.add(entry);
			seen.add(entry.type);
		}

		for (Class<? extends Item> type : pd.windows.WndDebugItems.allItems()) {
			if (seen.contains(type) || skipped(type)) continue;
			String matchName = matchName(type);
			if (matchName == null) continue;
			//tier 只用于并列时的先后（等级更低/名字更短优先），补入项统一放在最高档
			pool.add(new WishCatalog.Entry(type, 4, matchName, "", false, null));
			seen.add(type);
		}

		//SPSEXPD: 独特物品（UniqueItem）绝大多数既不在图鉴也不在生成表里，
		//只能从 UniqueItems 的注册表补入（用户裁决：独特物品也要能许出来）
		for (pd.items.unique.UniqueItemId id : pd.items.unique.UniqueItemId.values()) {
			Item sample;
			try {
				sample = pd.items.unique.UniqueItems.create(id);
			} catch (Throwable t) {
				continue;
			}
			if (sample == null) continue;
			//占位独特物品（效果未实装、只靠 id 区分）用 newInstance 造不出对应的那一件，跳过
			if (sample instanceof pd.items.unique.PlaceholderUniqueItem) continue;

			Class<? extends Item> type = sample.getClass();
			if (seen.contains(type) || skipped(type)) continue;
			String display = sample.name();
			String match = display == null || display.isEmpty()
					? matchName(type)
					: display.toLowerCase(java.util.Locale.ENGLISH) + " " + matchName(type);
			pool.add(new WishCatalog.Entry(type, 4, match, "", false, null));
			seen.add(type);
		}

		//剧情/任务/角色专属的特殊物品（由 EXTRA_SPECIAL 登记）
		for (Class<? extends Item> type : EXTRA_SPECIAL) {
			if (seen.contains(type) || skipped(type)) continue;
			String matchName = matchName(type);
			if (matchName == null) continue;
			pool.add(new WishCatalog.Entry(type, 4, matchName, "", false, null));
			seen.add(type);
		}

		matchPool = pool;
		return matchPool;
	}

	/**
	 * SPSEXPD: 剧情/任务/角色专属的「特殊物品」——图鉴、生成表与调试器都不收录这些，
	 * 但用户要求万能许愿器对它们也生效，所以在这里逐个登记（重复的会被 seen 去掉）。
	 */
	private static final Class<? extends Item>[] EXTRA_SPECIAL = new Class[]{
			pd.items.TreasureMap.class,
			pd.items.TengusMask.class,
			pd.items.KingsCrown.class,
			pd.items.Amulet.class,
			pd.items.DolyaSlate.class,
			pd.items.Triforce.class,
			pd.items.TriforceOfCourage.class,
			pd.items.TriforceOfPower.class,
			pd.items.TriforceOfWisdom.class,
			pd.items.SoulCollect.class,
			pd.items.ChallengeBook.class,
			pd.items.KnowledgeBook.class,
			pd.items.PotKey.class,
			pd.items.ShadowEaterKey.class,
			pd.items.BossRush.class,
			pd.items.Playericon.class,
			pd.items.BrokenSeal.class,
			pd.items.ArmorKit.class,
			pd.items.equipment.weapon.SpiritBow.class
	};

	/** 池子里明确不要的东西：货币（堆叠数值没意义）与测试装备。 */
	private static boolean skipped(Class<? extends Item> type) {
		if (type == pd.items.Gold.class) return true;
		if (type.getSimpleName().startsWith("Test")) return true;
		return java.lang.reflect.Modifier.isAbstract(type.getModifiers());
	}

	/** 匹配名 = 显示名（小写）+ 类名（小写，内部类以空格连接）。 */
	private static String matchName(Class<? extends Item> type) {
		String fqn = type.getName();
		int dot = fqn.lastIndexOf('.');
		String simple = dot < 0 ? fqn : fqn.substring(dot + 1);
		String fallback = simple.replace('$', ' ').toLowerCase(java.util.Locale.ENGLISH);
		String display = Messages.get(type, "name");
		if (display == null || display.isEmpty() || Messages.NO_TEXT_FOUND.equals(display)) {
			return fallback;
		}
		return display.toLowerCase(java.util.Locale.ENGLISH) + " " + fallback;
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
