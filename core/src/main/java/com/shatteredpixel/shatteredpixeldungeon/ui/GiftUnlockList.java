/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.GiftUnlocks;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGiftUnlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

/** 礼物商店的解锁列表（对照 SPS 0.9.9 GiftUnlockList）。 */
public class GiftUnlockList extends ScrollPane {

	private final ArrayList<ListItem> items = new ArrayList<>();

	public GiftUnlockList() {
		super( new Component() );

		for (GiftUnlocks.GiftUnlock unlock : GiftUnlocks.filtered()) {
			ListItem item = new ListItem( unlock );
			content.add( item );
			items.add( item );
		}
	}

	@Override
	protected void layout() {
		float pos = 0;
		for (ListItem item : items) {
			item.setRect( 0, pos, width, ListItem.HEIGHT );
			pos += ListItem.HEIGHT;
		}
		content.setSize( width, pos );
		super.layout();
	}

	@Override
	public void onClick( float x, float y ) {
		for (ListItem item : items) {
			if (item.onClick( x, y )) break;
		}
	}

	private class ListItem extends Component {

		private static final float HEIGHT = 18;

		private final GiftUnlocks.GiftUnlock unlock;

		private Image icon;
		private RenderedTextBlock label;
		private Image check;

		public ListItem( GiftUnlocks.GiftUnlock unlock ) {
			super();
			this.unlock = unlock;
			icon.copy( BadgeBanner.image( unlock.image ) );
			label.text( unlock.title() );
			check.copy( Icons.get( Icons.CHECKED ) );
		}

		@Override
		protected void createChildren() {
			icon = new Image();
			add( icon );

			label = PixelScene.renderTextBlock( 6 );
			add( label );

			check = new Image();
			add( check );
		}

		@Override
		protected void layout() {
			icon.x = x;
			icon.y = y + (height - icon.height) / 2;
			PixelScene.align( icon );

			label.setPos(
					icon.x + icon.width + 2,
					y + (height - label.height()) / 2
			);
			PixelScene.align( label );

			check.x = x + width - check.width;
			check.y = y + (height - check.height) / 2;
			PixelScene.align( check );
			//已购亮显，未购暗显（对照 0.9.9 的 shopcheck 透明度）
			check.alpha( GiftUnlocks.isUnlocked( unlock ) ? 1f : 0.3f );
		}

		public boolean onClick( float x, float y ) {
			if (inside( x, y )) {
				Sample.INSTANCE.play( Assets.Sounds.CLICK, 0.7f, 0.7f, 1.2f );
				Game.scene().addToFront( new WndGiftUnlock( unlock ) );
				return true;
			}
			return false;
		}
	}
}
