package pd.journal;

import watabou.noosa.Game;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/** Verifies the reachable SPS mainland guide and its original translations. */
public final class SpsStoryGuideTest {

	private static final List<String> PAGES = Arrays.asList(
			"Intro", "tester", "tower", "def", "palace", "ruins", "horn",
			"tribe", "homeless", "overseas", "damagetype", "enemy", "weapon");

	public static void main(String[] args) throws Exception {
		Game.version = "test";
		testDocumentPages();
		testJournalEntry();
		testOriginalTranslations();
		System.out.println("SPS大陆文档测试通过：13页顺序、日志入口、默认可读状态及英简繁俄UTF-8原文均正常。");
	}

	private static void testDocumentPages() {
		Document guide = Document.STORY_GUIDE;
		check(new ArrayList<>(guide.pageNames()).equals(PAGES), "SPS大陆文档页面顺序与0.9.8不一致");
		for (String page : PAGES) {
			check(guide.isPageFound(page) && guide.isPageRead(page), page + "没有默认开放并标记为已读");
		}
	}

	private static void testJournalEntry() throws IOException {
		Path source = Path.of("..", "java", "pd",
				"windows", "WndJournal.java");
		String window = Files.readString(source, StandardCharsets.UTF_8);
		check(window.contains("addDocument(Document.STORY_GUIDE)"), "当前日志指南页没有接入SPS大陆文档");
	}

	private static void testOriginalTranslations() throws Exception {
		Path legacy = Path.of("..", "..", "..", "..", "..", "SPS-PD-0.9.8", "SPS-PD-0.9.8",
				"resources", "com", "hmdzl", "spspd", "messages", "misc");
		if (!legacy.toFile().exists()) {
			//外部 0.9.8 基准目录缺失时回退到仓库内置参考源码
			legacy = Path.of("..", "..", "..", "..", "_ref", "ref", "SPS-PD",
					"resources", "com", "hmdzl", "spspd", "messages", "misc");
		}
		compareLocale(legacy.resolve("misc.properties"), Path.of("messages", "journal", "en", "journal.properties"));
		compareLocale(legacy.resolve("misc_zh.properties"), Path.of("messages", "journal", "zh", "journal.properties"));
		compareLocale(legacy.resolve("misc_tzh.properties"), Path.of("messages", "journal", "zh-hant", "journal.properties"));
		compareLocale(legacy.resolve("misc_ru.properties"), Path.of("messages", "journal", "ru", "journal.properties"));
	}

	private static void compareLocale(Path legacyPath, Path currentPath) throws Exception {
		assertStrictUtf8(legacyPath);
		assertStrictUtf8(currentPath);
		Properties legacy = load(legacyPath);
		Properties current = load(currentPath);
		String oldPrefix = "infos.newdocument.story_guide.";
		String newPrefix = "journal.document.story_guide.";
		check(legacy.getProperty(oldPrefix + "title").equals(current.getProperty(newPrefix + "title")),
				currentPath + "的文档标题与旧版不一致");
		for (String page : PAGES) {
			String normalized = page.toLowerCase();
			for (String field : Arrays.asList("title", "body")) {
				String suffix = normalized + "." + field;
				String expected = legacy.getProperty(oldPrefix + suffix);
				String actual = current.getProperty(newPrefix + suffix);
				check(expected != null && expected.equals(actual), currentPath + "缺少或改动了旧版键 " + suffix);
			}
		}
	}

	private static Properties load(Path path) throws IOException {
		Properties properties = new Properties();
		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			properties.load(reader);
		}
		return properties;
	}

	private static void assertStrictUtf8(Path path) throws IOException {
		try {
			StandardCharsets.UTF_8.newDecoder()
					.onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT)
					.decode(java.nio.ByteBuffer.wrap(Files.readAllBytes(path)));
		} catch (CharacterCodingException error) {
			throw new AssertionError(path + "不是有效UTF-8", error);
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsStoryGuideTest() { }
}
