/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.items.equipment.weapon.melee.special.ErrorW;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.utils.GLog;

/** XixiZero's original two-answer Egoal dialog. */
public class WndEgoalInfo extends Window {
	private static final int WIDTH = 120;

	public WndEgoalInfo() {
		ErrorW icon = new ErrorW();
		IconTitle title = new IconTitle(new ItemSprite(icon.image(), null),
				Messages.titleCase(Messages.get(this, "title")));
		title.setRect(0, 0, WIDTH, 0);
		add(title);

		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "info1"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + 2);
		add(message);

		RedButton yes = new RedButton(Messages.get(this, "yes")) {
			@Override protected void onClick() {
				hide();
				GLog.n(Messages.get(WndEgoalInfo.class, "tell1"));
			}
		};
		yes.setRect(0, message.bottom() + 2, WIDTH, 20);
		add(yes);

		RedButton no = new RedButton(Messages.get(this, "no")) {
			@Override protected void onClick() {
				hide();
				GLog.n(Messages.get(WndEgoalInfo.class, "tell2"));
			}
		};
		no.setRect(0, yes.bottom() + 2, WIDTH, 20);
		add(no);
		resize(WIDTH, (int) no.bottom());
	}
}
