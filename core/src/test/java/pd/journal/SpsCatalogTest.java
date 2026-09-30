package pd.journal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.items.Item;
import render.noosa.Game;
import render.utils.serialize.Reflection;

import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Checks the complete, player-visible SPS-PD 0.9.8 item catalog. */
public final class SpsCatalogTest {

	private static final Pattern ENTRY = Pattern.compile(
			"([A-Z]+)\\.seen\\.put\\(\\s*([A-Za-z0-9_]+)(\\.Seed)?\\.class");

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		testExactLegacyLists();
		testConstructionAndUiEntry();
		testUtf8Titles();
		System.out.println("SPS物品目录测试通过：七组371个旧版条目、顺序、实例化、当前日志入口及多语言UTF-8标题均正常。");
	}

	private static void testExactLegacyLists() throws Exception {
		Path oldSource = Path.of("..", "..", "..", "..", "..", "SPS-PD-0.9.8", "SPS-PD-0.9.8",
				"java", "com", "hmdzl", "spspd", "infos", "NewCatalog.java");
		if (!Files.exists(oldSource)) {
			//外部 0.9.8 基准目录缺失时回退到仓库内置参考源码（内容同为 0.9.8 NewCatalog.java）
			oldSource = Path.of("..", "..", "..", "..", "_ref", "ref", "SPS-PD",
					"java", "com", "hmdzl", "spspd", "infos", "NewCatalog.java");
		}
		Map<SpsCatalog, List<String>> expected = new EnumMap<>(SpsCatalog.class);
		for (SpsCatalog catalog : SpsCatalog.values()) expected.put(catalog, new ArrayList<>());
		for (String line : Files.readAllLines(oldSource, StandardCharsets.UTF_8)) {
			if (line.trim().startsWith("//")) continue;
			Matcher matcher = ENTRY.matcher(line);
			if (matcher.find()) {
				SpsCatalog catalog = SpsCatalog.valueOf(matcher.group(1));
				expected.get(catalog).add(matcher.group(2) + (matcher.group(3) == null ? "" : ".Seed"));
			}
		}

		int total = 0;
		for (SpsCatalog catalog : SpsCatalog.values()) {
			List<String> actual = new ArrayList<>();
			for (Class<? extends Item> type : catalog.items()) actual.add(legacyName(type));
			check(expected.get(catalog).equals(actual), catalog + "目录条目或顺序与0.9.8不一致");
			check(catalog.totalSeen() == catalog.totalItems(), catalog + "没有按旧版默认全部可见");
			total += catalog.totalItems();
		}
		check(total == 371, "SPS物品目录总数错误: " + total);
	}

	private static String legacyName(Class<?> type) {
		String name;
		if (type.getSimpleName().equals("Seed") && type.getEnclosingClass() != null) {
			name = type.getEnclosingClass().getSimpleName() + ".Seed";
		} else {
			name = type.getSimpleName();
		}
		if (name.equals("WarDrum")) return "Wardrum";
		if (name.equals("MagicPill")) return "Magicpill";
		if (name.equals("TimePill")) return "Timepill";
		return name;
	}

	private static void testConstructionAndUiEntry() throws Exception {
		for (SpsCatalog catalog : SpsCatalog.values()) {
			for (Class<? extends Item> type : catalog.items()) {
				check(Reflection.newInstance(type) != null, "目录物品无法实例化: " + type.getName());
			}
		}
		Path window = Path.of("..", "java", "pd",
				"windows", "WndJournal.java");
		String source = Files.readString(window, StandardCharsets.UTF_8);
		check(source.contains("SpsCatalog.EQUIPMENT") && source.contains("SpsCatalog.CONSUMABLES"),
				"当前日志仍未使用完整SPS物品目录");
	}

	private static void testUtf8Titles() throws Exception {
		for (String file : new String[]{"en/journal.properties", "zh/journal.properties",
				"zh-hant/journal.properties", "ru/journal.properties"}) {
			Path path = Path.of("messages", "journal", file);
			String text = StandardCharsets.UTF_8.newDecoder()
					.onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT)
					.decode(java.nio.ByteBuffer.wrap(Files.readAllBytes(path))).toString();
			for (SpsCatalog catalog : SpsCatalog.values()) {
				check(text.contains("journal.spscatalog." + catalog.name().toLowerCase() + ".title="),
						file + "缺少" + catalog + "目录标题");
			}
			check(text.indexOf('\uFFFD') < 0, file + "含UTF-8替换字符");
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsCatalogTest() { }
}
