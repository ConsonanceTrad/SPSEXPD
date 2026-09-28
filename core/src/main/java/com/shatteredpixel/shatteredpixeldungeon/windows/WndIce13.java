/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.ChaosPack;
import com.shatteredpixel.shatteredpixeldungeon.items.PowerHand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

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
