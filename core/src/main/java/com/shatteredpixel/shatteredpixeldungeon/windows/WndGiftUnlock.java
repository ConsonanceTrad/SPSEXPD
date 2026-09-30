/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.GiftUnlocks;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GiftShopScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

/** 礼物商店的单个解锁购买窗口（对照 SPS 0.9.9 WndGiftUnlock）。 */
public class WndGiftUnlock extends Window {

	private static final int WIDTH = 120;
	private static final int MARGIN = 2;

	public WndGiftUnlock( final GiftUnlocks.GiftUnlock unlock ) {
		super();

		Image icon = BadgeBanner.image( unlock.image );

		IconTitle titlebar = new IconTitle( icon, unlock.title() );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );

		RenderedTextBlock desc = PixelScene.renderTextBlock( unlock.desc(), 6 );
		desc.maxWidth( WIDTH - MARGIN * 2 );
		desc.setPos( MARGIN, titlebar.bottom() + MARGIN );
		add( desc );

		float pos = desc.bottom() + MARGIN;

		if (!GiftUnlocks.isUnlocked( unlock )) {
			RenderedTextBlock price = PixelScene.renderTextBlock(
					Messages.get( GiftUnlocks.class, "price", unlock.price ), 6 );
			price.maxWidth( WIDTH - MARGIN * 2 );
			price.setPos( MARGIN, pos );
			add( price );

			RedButton btnBuy = new RedButton( Messages.get( this, "buy" ) ) {
				@Override
				protected void onClick() {
					if (SPDSettings.sCoin() >= unlock.price) {
						GiftUnlocks.buyOneGift( unlock );
						SPDSettings.sCoinSpend( unlock.price );
						GiftUnlocks.saveGlobal();
						hide();
						//SPS: 场景内刷新以保留列表滚动位置（用户裁决 2026-09-30）
						if (Game.scene() instanceof GiftShopScene) {
							((GiftShopScene) Game.scene()).refresh();
						} else {
							ShatteredPixelDungeon.switchNoFade( GiftShopScene.class );
						}
					} else {
						hide();
						Game.scene().add( new WndMessage( Messages.get( WndGiftUnlock.class, "more_gold" ) ) );
					}
				}
			};
			btnBuy.setRect( 0, price.bottom() + MARGIN + 1, 50, 20 );
			add( btnBuy );

			resize( WIDTH, (int)(btnBuy.bottom() + MARGIN) );
		} else {
			resize( WIDTH, (int)(pos + MARGIN) );
		}
	}
}
