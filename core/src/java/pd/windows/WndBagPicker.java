/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import java.util.ArrayList;

import pd.Dungeon;
import pd.items.equipment.bags.Bag;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.ui.IconButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import render.noosa.Game;

/**
 * SPSEXPD: 「快捷背包」面板——开启快捷背包后，单击 HUD 背包按钮弹出的浮动包裹选择界面。
 *
 * <p>每行 {@link #COLS} 个包裹图标（主背包恒为第一格，与标签栏顺序一致），点击即打开对应的
 * {@link WndBag}。相比把标签挂在背包窗口左右两侧，这里不占用背包窗口的宽度预算，
 * 高缩放下背包格子就不必缩小。</p>
 */
public class WndBagPicker extends Window {

	private static final int COLS   = 4;    //每行 4 个
	private static final int CELL   = 24;   //图标格边长
	private static final int GAP    = 2;
	private static final int MARGIN = 6;    //窗口内容边距
	private static final int TITLE  = 16;   //标题行高

	static {
		InlineText.of(WndBagPicker.class)
			.t("title", "选择包裹");
	}

	//SPSEXPD: 与 WndBag.INSTANCE 同理——只允许一个面板，并供 Toolbar 判断“双击”
	public static WndBagPicker INSTANCE;

	@Override
	public void hide() {
		super.hide();
		if (INSTANCE == this) {
			INSTANCE = null;
		}
	}

	public WndBagPicker() {
		super();

		if (INSTANCE != null) {
			INSTANCE.hide();
		}
		INSTANCE = this;

		ArrayList<Bag> bags = (Dungeon.hero != null)
				? Dungeon.hero.belongings.getBags()
				: new ArrayList<Bag>();
		int n = Math.max( 1, bags.size() );
		int rows = (n + COLS - 1) / COLS;

		int contentW = COLS * CELL + (COLS - 1) * GAP;
		int contentH = rows * CELL + (rows - 1) * GAP;
		int w = contentW + MARGIN * 2;
		int h = TITLE + contentH + MARGIN * 2;

		RenderedTextBlock title = PixelScene.renderTextBlock(
				Messages.titleCase( Messages.get(WndBagPicker.class, "title") ), 9 );
		title.hardlight( TITLE_COLOR );
		title.setPos( MARGIN, 4 );
		add( title );

		int idx = 0;
		for (Bag bag : bags) {
			if (bag == null) continue;

			final Bag target = bag;
			IconButton btn = new IconButton( WndBag.icon( bag ) ) {
				@Override
				protected void onClick() {
					hide();
					WndBag w = new WndBag( target );
					if (Game.scene() instanceof GameScene) {
						GameScene.show( w );
					} else {
						Game.scene().addToFront( w );
					}
				}
			};
			btn.setRect(
					MARGIN + (idx % COLS) * (CELL + GAP),
					TITLE + MARGIN - 2 + (idx / COLS) * (CELL + GAP),
					CELL, CELL );
			add( btn );
			idx++;
		}

		resize( w, h );
	}
}
