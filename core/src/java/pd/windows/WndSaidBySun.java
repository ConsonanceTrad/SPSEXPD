/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.Dungeon;
import pd.items.CurseBlood;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.utils.GLog;

/** SaidBySun's original 100,000-gold material purchase. */
public class WndSaidBySun extends Window {
	private static final int WIDTH = 120;
	public WndSaidBySun() {
		CurseBlood reward = new CurseBlood();
		IconTitle title = new IconTitle(new ItemSprite(reward.image(), null), Messages.titleCase(reward.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);
		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + 2);
		add(message);
		RedButton buy = new RedButton(Messages.get(this, "buy")) {
			@Override protected void onClick() {
				if (Dungeon.gold >= 100000) {
					Dungeon.gold -= 100000;
					if (!reward.doPickUp(Dungeon.hero)) Dungeon.level.drop(reward, Dungeon.hero.pos).sprite.drop();
				} else {
					GLog.w(Messages.get(WndSaidBySun.class, "more_gold"));
				}
				hide();
			}
		};
		buy.setRect(0, message.bottom() + 2, WIDTH, 20);
		add(buy);
		resize(WIDTH, (int) buy.bottom());
	}
}
