/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import java.util.ArrayList;

import pd.Chrome;
import pd.Dungeon;
import pd.items.equipment.bags.Bag;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.ui.IconButton;
import render.noosa.Game;
import render.noosa.NinePatch;
import render.noosa.ui.Component;

/**
 * SPSEXPD: 「快捷背包」面板——开启快捷背包后，单击 HUD 背包按钮弹出的包裹选择选框。
 *
 * <p>每行 {@link #COLS} 个包裹图标（主背包恒为第一格，与标签栏顺序一致），点击即打开对应的
 * {@link WndBag}。它**不居中**，而是紧贴在 HUD 背包按钮的正上方（水平以按钮为中心、底边贴住
 * 按钮顶边），并做成紧凑尺寸，尽量少遮挡画面。</p>
 *
 * <p><b>为什么不是 Window</b>（用户要求：打开选框时不影响任何其它区域的点击）：
 * {@code Window} 天生带全屏 blocker、暗化 shadow，以及 onBackPressed/键盘拦截，
 * 即使逐个移除也仍有残留副作用。所以这里与 {@code pd.ui.InventoryPane}（桌面端侧栏）用同一套做法——
 * 直接继承 {@code render.noosa.ui.Component}，挂到 {@code GameScene} 顶层：
 * 它只在自己的矩形内响应指针事件，其余区域完全不受影响。</p>
 *
 * <p>相比把标签挂在背包窗口左右两侧，这里不占用背包窗口的宽度预算，高缩放下背包格子就不必缩小。</p>
 */
public class WndBagPicker extends Component {

	private static final int COLS   = 5;    //每行 5 个
	private static final int CELL   = 16;   //格子 = 图标本身（16px），图标间的净间隔全部由 GAP 决定
	private static final int GAP    = 3;    //图标之间的间隔
	private static final int MARGIN = 1;    //面板内部额外留白（外框留白由 NinePatch 的 margin 提供）

	//SPSEXPD: 与 WndBag.INSTANCE 同理——只允许一个面板，并供 Toolbar 判断“双击”
	public static WndBagPicker INSTANCE;

	//HUD 背包按钮中心（uiCamera 逻辑坐标）与其顶边
	private final float anchorX;
	private final float anchorY;

	public WndBagPicker( float anchorX, float anchorY ) {
		super();

		this.anchorX = anchorX;
		this.anchorY = anchorY;

		if (INSTANCE != null) {
			INSTANCE.close();
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
		int h = contentH + MARGIN * 2;

		int idx = 0;
		for (Bag bag : bags) {
			if (bag == null) continue;

			final Bag target = bag;
			IconButton btn = new IconButton( WndBag.icon( bag ) ) {
				@Override
				protected void onClick() {
					close();
					WndBag wnd = new WndBag( target );
					if (Game.scene() instanceof GameScene) {
						GameScene.show( wnd );
					} else {
						Game.scene().addToFront( wnd );
					}
				}
			};
			btn.setRect(
					MARGIN + (idx % COLS) * (CELL + GAP),
					MARGIN + (idx / COLS) * (CELL + GAP),
					CELL, CELL );
			add( btn );
			idx++;
		}

		//SPSEXPD: 背景框——与 pd.ui.InventoryPane 同款的轻量面板底板（用户要求"要有背景框"）
		NinePatch bg = Chrome.get( Chrome.Type.TOAST_TR_HEAVY );
		bg.size( w + bg.marginHor(), h + bg.marginVer() );
		bg.x = -bg.marginLeft();
		bg.y = -bg.marginTop();
		addToBack( bg );

		setSize( w + bg.marginHor(), h + bg.marginVer() );
		placeAboveButton( width, height );

		//SPSEXPD: 必须显式挂 uiCamera——GameScene 的所有直属 UI 子都这么做（menu/status/toolbar/inventory…）。
		//不设的话 Group 会沿用场景相机，面板会被画到世界坐标上，看起来就是"没显示"。
		camera = PixelScene.uiCamera;

		//SPSEXPD: 挂到场景顶层（与 InventoryPane 同层）。面板只是普通 Component，
		//没有 Window 的 blocker，所以只有落在自己矩形内的指针事件才会被它响应。
		if (Game.scene() instanceof GameScene) {
			Game.scene().addToFront( this );
		}
	}

	//SPSEXPD: 关闭 = 从场景里摘掉自己（Component 没有 Window.hide() 那套）
	public void close() {
		if (parent != null) {
			parent.remove( this );
		}
		if (INSTANCE == this) {
			INSTANCE = null;
		}
	}

	//SPSEXPD: 面板出现在背包按钮的「左上方」——右下角对齐按钮左上角，底边即快捷栏顶边。
	//面板挂在与 InventoryPane 同一层，坐标就是 uiCamera 的逻辑坐标，不需要 Window 那样做屏幕像素换算。
	private void placeAboveButton( float w, float h ) {
		float left = Math.max( 0, Math.min( anchorX - w, PixelScene.uiCamera.width - w ) );
		float top = Math.max( 0, Math.min( anchorY - h, PixelScene.uiCamera.height - h ) );

		setPos( Math.round( left ), Math.round( top ) );
	}
}
