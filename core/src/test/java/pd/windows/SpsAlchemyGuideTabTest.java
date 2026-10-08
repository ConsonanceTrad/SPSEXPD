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
		check(body.contains("Document.ALCHEMY_GUIDE.pageSprite(page)"), "炼金目录条目没有带上书页图标");
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

		//ScrollPane 会把内容 camera 绑到「当时」窗口在屏幕上的位置，而窗口位置是 resize() 里才定的：
		//resize 必须在前，否则内容会停在窗口未定位时的中心位置（表现为文本从中心向右下渲染）
		int resized = popup.indexOf("resize(");
		check(resized > 0 && resized < positioned,
				"单页窗口先给滚动面板定位再 resize，滚动内容会偏移到窗口中心");
		check(popup.contains("public void offset(") && popup.contains("positionPane()"),
				"单页窗口没有在 offset() 里重新定位滚动面板，窗口偏移变化后内容会错位");

		//全屏 PointerArea 挂 uiCamera，会抢在窗口内容之前派发，把配方物品槽的点击吃掉
		check(!popup.contains("new PointerArea("),
				"单页窗口加回了全屏拦截层，配方里的物品槽将点不出物品名");

		//滚动区里的按钮收不到自己的点击（PointerController 先吃掉），必须由 ScrollPane 子类转发
		check(popup.contains("new ScrollPane(content) {") && popup.contains("public void onClick(float x, float y)"),
				"单页窗口没有把点击转发给配方槽，物品图标点不出物品名");
		check(read("../java/pd/ui/QuickRecipe.java").contains("public boolean onClick(float x, float y)"),
				"QuickRecipe 没有暴露坐标版 onClick，滚动区里无法转发点击");
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
