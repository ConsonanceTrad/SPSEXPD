/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.Dungeon;
import pd.items.ChaosPack;
import pd.items.PowerHand;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.utils.GLog;

/** Ice13's original Power Hand exchange for the chaos contract. */
public class WndIce13 extends Window {
	private static final int WIDTH = 120;

	public WndIce13() {
		ChaosPack reward = new ChaosPack();
		IconTitle title = new IconTitle(new ItemSprite(reward.image(), null), Messages.titleCase(reward.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);

		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + 2);
		add(message);

		RedButton exchange = new RedButton(Messages.get(this, "buy")) {
			@Override protected void onClick() {
				PowerHand hand = Dungeon.hero.belongings.getItem(PowerHand.class);
				if (hand != null) {
					hand.detach(Dungeon.hero.belongings.backpack);
					if (!reward.doPickUp(Dungeon.hero)) {
						Dungeon.level.drop(reward, Dungeon.hero.pos).sprite.drop();
					}
				} else {
					GLog.w(Messages.get(WndIce13.class, "missing_hand"));
				}
				hide();
			}
		};
		exchange.setRect(0, message.bottom() + 2, WIDTH, 20);
		add(exchange);
		resize(WIDTH, (int)exchange.bottom());
	}
}
