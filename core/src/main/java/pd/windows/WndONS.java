package pd.windows;

import pd.Dungeon;
import pd.items.quest.GnollClothes;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;

public class WndONS extends Window {

	private static final int WIDTH = 120;

	public WndONS(final GnollClothes clothes) {
		IconTitle title = new IconTitle(new ItemSprite(clothes.image(), null),
				Messages.titleCase(clothes.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);

		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + 2);
		add(message);

		RedButton give = new RedButton(Messages.get(this, "give")) {
			@Override
			protected void onClick() {
				if (clothes.detach(Dungeon.hero.belongings.backpack) != null) Dungeon.gnollMission = true;
				hide();
			}
		};
		give.setRect(0, message.bottom() + 2, WIDTH, 20);
		add(give);
		resize(WIDTH, (int) give.bottom());
	}
}
