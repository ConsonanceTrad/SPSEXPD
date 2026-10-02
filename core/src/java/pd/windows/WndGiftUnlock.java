/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.GiftUnlocks;
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.effects.BadgeBanner;
import pd.messages.Messages;
import pd.scenes.GiftShopScene;
import pd.scenes.PixelScene;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import render.noosa.Game;
import render.noosa.Image;
import pd.messages.InlineText;

/** 礼物商店的单个解锁购买窗口（对照 SPS 0.9.9 WndGiftUnlock）。 */
public class WndGiftUnlock extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndGiftUnlock.class)
			.t("buy", "购买")
			.t("more_gold", "你的S金不足。");
	}




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
