/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.GiftUnlocks;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.GiftUnlockList;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Camera;
import com.watabou.noosa.NinePatch;

/**
 * SPS 礼物商店：用 S金购买永久强化解锁（对照 SPS 0.9.9 GiftShopScene）。
 * 从标题画面进入，退出时把购买结果落盘到 giftunlocks.dat。
 */
public class GiftShopScene extends PixelScene {

	private static final int MAX_PANE_WIDTH = 160;

	private RenderedTextBlock balance;
	private GiftUnlockList list;
	private float listX, listY, listW, listH;

	@Override
	public void create() {
		super.create();

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );

		int paneWidth = Math.min( MAX_PANE_WIDTH, w - 6 );
		int paneHeight = h - 30;

		NinePatch pane = Chrome.get( Chrome.Type.WINDOW );
		pane.size( paneWidth, paneHeight );
		pane.x = (w - paneWidth) / 2f;
		pane.y = (h - paneHeight) / 2f;
		add( pane );

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get( this, "title" ), 9 );
		title.hardlight( 0xFFFF44 );
		title.setPos( align( (w - title.width()) / 2f ), align( (pane.y - title.height()) / 2f ) );
		add( title );

		//SPS: 左上角显示 S金余额（0.9.9 的 "SB:" 显示，键随四语文本）
		balance = PixelScene.renderTextBlock(
				Messages.get( this, "balance", SPDSettings.sCoin() ), 9 );
		balance.hardlight( 0xFFFF44 );
		balance.setPos( 0, align( (pane.y - balance.height()) / 2f ) );
		add( balance );

		GiftUnlocks.loadGlobal();
		list = new GiftUnlockList();
		add( list );
		listX = pane.x + pane.marginLeft();
		listY = pane.y + pane.marginTop();
		listW = pane.innerWidth();
		listH = pane.innerHeight();
		list.setRect( listX, listY, listW, listH );

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		fadeIn();
	}

	/**
	 * 购买后刷新：更新余额与上架列表，并保留滚动位置（用户裁决 2026-09-30，
	 * 不再整体重建场景回到列表开头）。
	 */
	public void refresh() {
		float scrollY = list.content().camera.scroll.y;

		list.destroy();
		remove( list );
		list = new GiftUnlockList();
		add( list );
		list.setRect( listX, listY, listW, listH );
		list.scrollTo( 0, scrollY );

		balance.text( Messages.get( this, "balance", SPDSettings.sCoin() ) );
	}

	@Override
	public void destroy() {
		GiftUnlocks.saveGlobal();
		super.destroy();
	}
}
