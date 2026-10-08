package pd.windows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * SPSEXPD: 日志（事件记录）去掉图鉴与徽章面板的无图形校验。
 *
 * <p>这两个面板打开时都要全量扫描目录/成就，进日志就卡；现在桌面场景（JournalScene）与小屏窗口（WndJournal）
 * 都不再构建它们，页签索引固定为 探险手册=0 / 指南=1 / 炼金=2，任何把索引指向 3、4 的引用都会让 select 越界。
 * 另外地牢指南里也不再收录「SPS大陆介绍」。</p>
 */
public final class SpsJournalTabsTest {

	public static void main(String[] args) throws Exception {
		testDesktopScene();
		testSmallWindow();
		testGuideDropsStoryGuide();
		System.out.println("SPS日志页签测试通过：日志已去掉徽章与图鉴面板与「SPS大陆介绍」，页签索引只留探险手册/指南/炼金，无越界引用。");
	}

	private static void testGuideDropsStoryGuide() throws Exception {
		String journal = read("../java/pd/windows/WndJournal.java");
		check(!journal.contains("addDocument(Document.STORY_GUIDE)"),
				"地牢指南里仍收录了「SPS大陆介绍」");
		check(journal.contains("addDocument(Document.ADVENTURERS_GUIDE)"),
				"地牢指南丢了「地牢探索指南」");
	}

	private static void testDesktopScene() throws Exception {
		String scene = read("../java/pd/scenes/JournalScene.java");
		check(!scene.contains("BadgesTab"), "桌面日志场景仍会构建徽章面板");
		check(!scene.contains("CatalogTab"), "桌面日志场景仍会构建图鉴面板");
		check(scene.contains("int lastIDX = 2"), "桌面日志默认没有落在指南（lastIDX 初值不是 2）");
		check(!scene.contains("lastIDX = 0") && !scene.contains("lastIDX = 1"),
				"桌面日志仍有指向已移除面板的 lastIDX 赋值");
	}

	private static void testSmallWindow() throws Exception {
		String journal = read("../java/pd/windows/WndJournal.java");
		check(!journal.contains("new CatalogTab()") && !journal.contains("new BadgesTab()"),
				"小屏日志窗口仍会构建图鉴/徽章页签");
		check(journal.contains("if (value) last_index = 0;")
						&& journal.contains("if (value) last_index = 1;")
						&& journal.contains("if (value) last_index = 2;"),
				"小屏日志的页签索引不是 0/1/2（探险手册/指南/炼金）");
		check(!journal.contains("last_index = 3") && !journal.contains("last_index = 4"),
				"仍有代码把日志索引指向已移除的图鉴/徽章页");

		//其它调用点也不能再指向被移除的页
		for (String path : new String[]{
				"../java/pd/ui/MenuPane.java",
				"../java/pd/scenes/PixelScene.java",
				"../java/pd/items/specific/journal/DocumentPage.java"}) {
			String src = read(path);
			check(!src.contains("WndJournal.last_index = 3") && !src.contains("WndJournal.last_index = 4"),
					path + " 仍把日志索引指向已移除的图鉴/徽章页");
			check(!src.contains("CatalogTab.currentItemIdx") && !src.contains("BadgesTab.global"),
					path + " 仍在写已移除面板的静态字段");
		}
	}

	private static String read(String path) throws Exception {
		return new String(Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
