/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.food.meatfood.FunnyFood;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;

/** DreamPlayer's original reset-to-level-one offer. */
public class WndDream extends Window {
	private static final int WIDTH = 120;

	public WndDream() {
		FunnyFood icon = new FunnyFood();
		IconTitle title = new IconTitle(new ItemSprite(icon.image(), null), Messages.titleCase(icon.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);

		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + 2);
		add(message);

		RedButton reset = new RedButton(Messages.get(this, "buy")) {
			@Override protected void onClick() {
				applyReset(Dungeon.hero);
				hide();
			}
		};
		reset.setRect(0, message.bottom() + 2, WIDTH, 20);
		add(reset);
		resize(WIDTH, (int) reset.bottom());
	}

	public static void applyReset(Hero hero) {
		if (hero == null) return;
		int oldLevel = hero.lvl;
		hero.improveCombatSkills(-(oldLevel - 1));
		hero.lvl = 1;
		hero.HTBoost = 20;
		hero.updateHT(false);
	}
}
