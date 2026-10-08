package pd.windows;

import pd.Chrome;
import pd.atlas.items.SpecificPagesDict;
import pd.journal.Document;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.QuickRecipe;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import pd.ui.Window;
import render.input.PointerEvent;
import render.noosa.ColorBlock;
import render.noosa.PointerArea;
import render.noosa.ui.Component;

import java.util.ArrayList;

/**
 * SPSEXPD: 炼金指南的某一页——标题固定在上方，正文与该页收录的配方放在一个可滚动区域里。
 *
 * <p>炼金指南的页签现在只有一份可滚动的文字目录，每一页的详细内容改由这个窗口弹出显示。
 * 这样以后新增配方组时目录条目只是多一行，不再需要往页签顶部挤图标按钮。</p>
 */
public class WndAlchemyPage extends Window {

	private static final int WIDTH_P = 125;
	private static final int WIDTH_L = 180;
	private static final int MARGIN = 2;
	/** 标题和上下留白之外，滚动区域可以占用的高度。 */
	private static final int CHROME_HEIGHT = 24;

	public WndAlchemyPage(int pageIdx) {
		super(0, 0, Chrome.get(Chrome.Type.SCROLL));

		int width = (PixelScene.landscape() ? WIDTH_L : WIDTH_P) - MARGIN * 2;

		//先铺一层拦截器：点在滚动区域之外（以及窗口外）就把这一页关掉。
		//它必须在滚动面板之前 add，z 序低于滚动面板，否则会把滚动手势一起吃掉。
		PointerArea blocker = new PointerArea(0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height) {
			@Override
			protected void onClick(PointerEvent event) {
				onBackPressed();
			}
		};
		blocker.camera = PixelScene.uiCamera;
		add(blocker);

		IconTitle title = new IconTitle(new ItemSprite(SpecificPagesDict.ALCH_PAGE_0),
				Document.ALCHEMY_GUIDE.pageTitle(pageIdx));
		title.setRect(0, 0, width, 0);
		title.tfLabel.invert();
		add(title);

		float top = title.bottom() + MARGIN;

		Component content = new Component();

		RenderedTextBlock body = PixelScene.renderTextBlock(6);
		body.maxWidth(width);
		body.text(Document.ALCHEMY_GUIDE.pageBody(pageIdx));
		body.invert();
		body.setPos(0, 0);
		content.add(body);

		float bottom = layoutRecipes(content, pageIdx, width, body.bottom() + 3);
		content.setSize(width, bottom);

		float maxHeight = (PixelScene.landscape() ? PixelScene.MIN_HEIGHT_L : PixelScene.MIN_HEIGHT_P)
				- CHROME_HEIGHT - top;
		float paneHeight = Math.max(1, Math.min(bottom, maxHeight));

		ScrollPane pane = new ScrollPane(content);
		add(pane);
		//注意顺序：setRect 会立刻走 ScrollPane.layout()，那里要沿父链找 Camera，
		//所以必须先 add 进窗口再定位，否则 addToFront 之前就 NPE（见 WndDailies 的写法）
		pane.setRect(MARGIN, top, width, paneHeight);

		resize(width + MARGIN * 2, (int)(top + paneHeight) + MARGIN);
	}

	/**
	 * 把 {@link QuickRecipe#getRecipes(int)} 给的序列排成居中的一行行；null 表示换行间隔。
	 * 返回排完之后的底部 y（内容坐标系）。
	 */
	private float layoutRecipes(Component content, int pageIdx, int width, float top) {
		ArrayList<QuickRecipe> toAdd = QuickRecipe.getRecipes(pageIdx);
		ArrayList<QuickRecipe> row = new ArrayList<>();

		while (!toAdd.isEmpty()) {
			if (toAdd.get(0) == null) {
				toAdd.remove(0);
				top += 6;
			}

			int w = 0;
			while (!toAdd.isEmpty() && toAdd.get(0) != null && w + toAdd.get(0).width() <= width) {
				row.add(toAdd.remove(0));
				w += row.get(0).width();
			}

			float spacing = (width - w) / (row.size() + 1f);
			float left = spacing;
			while (!row.isEmpty()) {
				QuickRecipe r = row.remove(0);
				r.setPos(left, top);
				left += r.width() + spacing;
				if (!row.isEmpty()) {
					ColorBlock spacer = new ColorBlock(1, 16, 0xFF222222);
					spacer.y = top;
					spacer.x = left - spacing / 2 - 0.5f;
					PixelScene.align(spacer);
					content.add(spacer);
				}
				content.add(r);
			}

			if (!toAdd.isEmpty() && toAdd.get(0) == null) {
				toAdd.remove(0);
			}

			if (!toAdd.isEmpty() && toAdd.get(0) != null) {
				ColorBlock spacer = new ColorBlock(width, 1, 0xFF222222);
				spacer.y = top + 16;
				spacer.x = 0;
				content.add(spacer);
			}

			top += 17;
		}

		return top - 1;
	}
}
