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
import render.noosa.ColorBlock;
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

	private ScrollPane pane;
	private final ArrayList<QuickRecipe> recipes = new ArrayList<>();
	private final int paneWidth;
	private final float paneTop;
	private final float paneHeight;

	public WndAlchemyPage(String page) {
		super(0, 0, Chrome.get(Chrome.Type.SCROLL));

		paneWidth = (PixelScene.landscape() ? WIDTH_L : WIDTH_P) - MARGIN * 2;

		IconTitle title = new IconTitle(new ItemSprite(SpecificPagesDict.ALCH_PAGE_0),
				Document.ALCHEMY_GUIDE.pageTitle(page));
		title.setRect(0, 0, paneWidth, 0);
		title.tfLabel.invert();

		paneTop = title.bottom() + MARGIN;

		Component content = new Component();

		RenderedTextBlock body = PixelScene.renderTextBlock(6);
		body.maxWidth(paneWidth);
		body.text(Document.ALCHEMY_GUIDE.pageBody(page));
		body.invert();
		body.setPos(0, 0);
		content.add(body);

		content.setSize(paneWidth, layoutRecipes(content, page, paneWidth, body.bottom() + 3));

		float maxHeight = (PixelScene.landscape() ? PixelScene.MIN_HEIGHT_L : PixelScene.MIN_HEIGHT_P)
				- CHROME_HEIGHT - paneTop;
		paneHeight = Math.max(1, Math.min(content.height(), maxHeight));

		//必须先 resize 再给滚动面板定位：ScrollPane.layout() 会把内容自己的 camera 绑到
		//「当时」窗口在屏幕上的位置，而窗口的居中位置是 Window.resize() 里才确定的。
		//顺序反了内容就会停在窗口未定位时的中心位置，表现为文本整体向右下偏移（见 WndDailies 的写法）。
		resize(paneWidth + MARGIN * 2, (int)(paneTop + paneHeight) + MARGIN);

		add(title);

		pane = new ScrollPane(content) {
			@Override
			public void onClick(float x, float y) {
				//SPSEXPD: 滚动区里的按钮收不到自己的点击（PointerController 会先吃掉），这里手动转发给配方槽
				for (QuickRecipe r : recipes) {
					if (r.onClick(x, y)) {
						break;
					}
				}
			}
		};
		add(pane);
		positionPane();

		//SPSEXPD: 这里刻意不加全屏 PointerArea 关闭层：它挂的是 uiCamera，会在事件派发里抢在窗口内容之前，
		//把 QuickRecipe 里物品槽自己的点击（弹出 WndInfoItem 显示物品名）一起吃掉。关闭走 ESC / 返回键。
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		//窗口偏移变化后重新定位，让滚动内容自己的 camera 跟上窗口当前位置
		//（Window 里那句 "windows with scroll panes will likely need to override this" 指的正是这里）
		if (pane != null) {
			positionPane();
		}
	}

	private void positionPane() {
		pane.setRect(MARGIN, paneTop, paneWidth, paneHeight);
	}

	/**
	 * 把 {@link QuickRecipe#getRecipes(String)} 给的序列排成居中的一行行；null 表示换行间隔。
	 * 返回排完之后的底部 y（内容坐标系）。
	 */
	private float layoutRecipes(Component content, String page, int width, float top) {
		ArrayList<QuickRecipe> toAdd = QuickRecipe.getRecipes(page);
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
				recipes.add(r);
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
