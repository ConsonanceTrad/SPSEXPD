/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.Dungeon;
import pd.items.Heap;
import pd.items.weapon.melee.block.SpKnuckles;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.utils.GLog;

/** Shower's legacy 3,000-gold special-knuckles shop. */
public class WndShower extends Window {
	private static final int WIDTH = 120;
	public static final int PRICE = 3000;

	public WndShower() {
		SpKnuckles reward = new SpKnuckles();
		IconTitle title = new IconTitle(new ItemSprite(reward.image(), null), Messages.titleCase(reward.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);
		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + 2);
		add(message);
		RedButton buy = new RedButton(Messages.get(this, "buy")) {
			@Override protected void onClick() {
				if (!purchase()) GLog.w(Messages.get(WndShower.class, "more_gold"));
				hide();
			}
		};
		buy.setRect(0, message.bottom() + 2, WIDTH, 20);
		add(buy);
		resize(WIDTH, (int)buy.bottom());
	}

	public static boolean purchase() {
		if (Dungeon.hero == null || Dungeon.gold <= PRICE) return false;
		Dungeon.gold -= PRICE;
		SpKnuckles reward = new SpKnuckles();
		if (!reward.collect(Dungeon.hero.belongings.backpack) && Dungeon.level != null) {
			Heap heap = Dungeon.level.drop(reward, Dungeon.hero.pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
		return true;
	}
}
