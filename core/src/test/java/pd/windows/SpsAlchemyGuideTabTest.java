package pd.windows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * SPSEXPD: 炼金指南页签布局的无图形校验。
 *
 * <p>页签必须是一份由 {@code Document.ALCHEMY_GUIDE.pageNames()} 驱动的可滚动文字目录，
 * 每一页的正文与配方由 {@link WndAlchemyPage} 弹窗显示；不能再退回"固定数量的图标按钮"，
 * 否则以后新增配方组时又会排不下。</p>
 */
public final class SpsAlchemyGuideTabTest {

	public static void main(String[] args) throws Exception {
		testTabIsScrollableTextIndex();
		testPopupHoldsBodyAndRecipes();
		testMissingPageText();
		System.out.println("SPS炼金指南页签测试通过：可滚动文字目录、点击弹出正文与配方、缺页文案双语均正常。");
	}

	private static void testTabIsScrollableTextIndex() throws Exception {
		String journal = read("../java/pd/windows/WndJournal.java");
		int tab = journal.indexOf("class AlchemyTab");
		check(tab > 0, "WndJournal 里找不到 AlchemyTab");

		String body = journal.substring(tab, journal.indexOf("class NotesTab", tab));

		check(body.contains("ScrollingListPane"), "炼金页签不是可滚动列表");
		check(body.contains("Document.ALCHEMY_GUIDE.pageNames()"),
				"炼金页签的目录没有遍历 Document.ALCHEMY_GUIDE 的页列表，新增页不会自动出现");
		check(body.contains("new WndAlchemyPage("), "炼金页签点击条目后没有弹出单页窗口");
		check(body.contains("readPage("), "翻开炼金页时没有标记为已读");
		check(body.contains("list.clear()"), "重建炼金目录前没有清空，重复布局会累积条目");

		//固定 9 个图标按钮是这次要淘汰的旧布局
		check(!body.contains("pageButtons"), "炼金页签仍残留按页生成的图标按钮");
		check(!body.contains("NUM_BUTTONS"), "炼金页签仍写死了页数常量");
		check(body.contains("currentPageIdx"), "炼金页签丢掉了外部（日志页道具）定位用的 currentPageIdx");
	}

	private static void testPopupHoldsBodyAndRecipes() throws Exception {
		String popup = read("../java/pd/windows/WndAlchemyPage.java");

		check(popup.contains("class WndAlchemyPage extends Window"), "单页窗口没有继承 Window");
		check(popup.contains("ScrollPane"), "单页窗口的正文与配方不能滚动");
		check(popup.contains("Document.ALCHEMY_GUIDE.pageBody("), "单页窗口没有显示页正文");
		check(popup.contains("Document.ALCHEMY_GUIDE.pageTitle("), "单页窗口没有显示页标题");
		check(popup.contains("QuickRecipe.getRecipes("), "单页窗口没有列出该页的配方");

		//setRect 会立刻触发 ScrollPane.layout()，它要沿父链找 Camera：必须先 add 再定位，
		//否则窗口还没进 scene 就 NPE（WndDailies 也是这么写的）
		int added = popup.indexOf("add(pane)");
		int positioned = popup.indexOf("pane.setRect(");
		check(added > 0 && positioned > added,
				"单页窗口先定位滚动面板再 add，打开时会因 camera() 为 null 而崩溃");
	}

	private static void testMissingPageText() throws Exception {
		String inline = read("../java/pd/windows/WndJournal.java");
		check(inline.contains(".t(\"$alchemytab.missing\""), "WndJournal 的内联文案缺少炼金页签的缺页文本");

		for (String lang : new String[]{"zh", "en"}) {
			String props = read("messages/windows/" + lang + "/windows.properties");
			check(props.contains("windows.wndjournal$alchemytab.missing="),
					lang + " 的 windows.properties 缺少炼金页签的缺页文本");
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
