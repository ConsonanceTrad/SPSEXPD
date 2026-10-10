/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import java.util.ArrayList;

import pd.Assets;
import pd.Dungeon;
import pd.items.equipment.bags.Bag;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.ui.Button;
import pd.ui.IconButton;
import pd.ui.Window;
import render.noosa.Game;
import render.noosa.Image;

/**
 * SPSEXPD: 「快捷背包」面板——开启快捷背包后，单击 HUD 背包按钮弹出的包裹选择选框。
 *
 * <p>每行 {@link #COLS} 个包裹图标（主背包恒为第一格，与标签栏顺序一致），点击即打开对应的
 * {@link WndBag}。它**不居中**，而是紧贴在 HUD 背包按钮的正上方（水平以按钮为中心、底边贴住
 * 按钮顶边），并做成紧凑尺寸，尽量少遮挡画面。</p>
 *
 * <p>相比把标签挂在背包窗口左右两侧，这里不占用背包窗口的宽度预算，高缩放下背包格子就不必缩小。</p>
 */
public class WndBagPicker extends Window {

	private static final int COLS   = 4;    //每行 4 个
	private static final int CELL   = 20;   //紧凑格边长（图标 16px + 少量留白）
	private static final int GAP    = 1;
	private static final int MARGIN = 3;

	//SPSEXPD: 与 WndBag.INSTANCE 同理——只允许一个面板，并供 Toolbar 判断“双击”
	public static WndBagPicker INSTANCE;

	private final float anchorX;   //HUD 背包按钮中心（uiCamera 逻辑坐标）
	private final float anchorY;   //HUD 背包按钮顶边

	public WndBagPicker( float anchorX, float anchorY ) {
		super();

		this.anchorX = anchorX;
		this.anchorY = anchorY;

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
		int h = contentH + MARGIN * 2;

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
					MARGIN + (idx / COLS) * (CELL + GAP),
					CELL, CELL );
			add( btn );
			idx++;
		}

		resize( w, h );

		//SPSEXPD: 面板整图——bg_liner.png（斜向底纹 + 右上角已画好的关闭按钮）按选框尺寸整体缩放。
		//不平铺（斜向图案平铺会看出接缝）、也不切分。默认的 Window 外框只隐藏不移除，
		//因为 resize() 与 camera 的尺寸计算仍然依赖 chrome 对象。
		Image panel = new Image( Assets.Interfaces.BG_LINER );
		panel.scale.set( w / panel.width, h / panel.height );
		addToBack( panel );
		chrome.visible = false;

		//SPSEXPD: 关闭按钮的点击区——只盖住图上按钮那一块（64x64 里的 x=48..58, y=0..10），
		//按下只关闭本选框（等同“收起”语义）
		float bx = 48f / 64f * w;
		float by = 0f;
		float bw = 11f / 64f * w;
		float bh = 11f / 64f * h;
		Button close = new Button() {
			@Override
			protected void onClick() {
				hide();
			}
		};
		close.setRect( Math.round( bx ), Math.round( by ),
				Math.max( 4, Math.round( bw ) ), Math.max( 4, Math.round( bh ) ) );
		add( close );

		//SPSEXPD: 做成“非模态”紧凑选框——去掉全屏 blocker 与整屏变暗。
		//原因：Window 默认的 blocker 覆盖全屏，会拦住 HUD 背包按钮的点击，
		//使“再点一次按钮（即双击）打开主背包”完全收不到事件。
		remove( blocker );
		remove( shadow );

		placeAboveButton();
	}

	//SPSEXPD: 非模态——不阻断下层输入（配合 Window.blocksInput() 与 GameScene.showingWindow()）
	@Override
	public boolean blocksInput() {
		return false;
	}

	@Override
	public void hide() {
		super.hide();
		if (INSTANCE == this) {
			INSTANCE = null;
		}
	}

	//SPSEXPD: 不居中——底边贴在 HUD 背包按钮正上方，水平以按钮为中心，并保证不出屏
	private void placeAboveButton() {
		float scale = 1f;
		if (PixelScene.uiCamera != null && PixelScene.uiCamera.width > 0) {
			//Window 的 camera 用屏幕像素坐标（Game.width/height），这里把 uiCamera 逻辑坐标换算过去
			scale = (float)Game.width / PixelScene.uiCamera.width;
		}

		int x = Math.round( anchorX * scale - camera.screenWidth() / 2f );
		int y = Math.round( anchorY * scale - camera.screenHeight() );

		//Camera.screenWidth()/screenHeight() 是 float，这里显式取整后再夹取
		int maxX = Game.width - Math.round( camera.screenWidth() );
		int maxY = Game.height - Math.round( camera.screenHeight() );
		x = Math.max( 0, Math.min( x, maxX ) );
		y = Math.max( 0, Math.min( y, maxY ) );

		camera.x = x;
		camera.y = y;
	}
}
