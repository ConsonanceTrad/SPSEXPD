/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.Dungeon;
import pd.items.CrystalVial;
import pd.levels.Level;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.messages.InlineText;

/** Original SPS confirmation shown before ending a run at the surface. */
public class WndAscend extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndAscend.class)
			.t("message", "你确定要离开吗？本次旅程将会结束。")
			.t("ok", "当然");
	}

	private static final int WIDTH = 120;

	public WndAscend() {
		CrystalVial icon = new CrystalVial();
		IconTitle title = new IconTitle(new ItemSprite(icon.image(), null), Messages.titleCase(icon.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);

		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + 2);
		add(message);

		RedButton confirm = new RedButton(Messages.get(this, "ok")) {
			@Override protected void onClick() {
				confirmDeparture(Dungeon.level);
				hide();
			}
		};
		confirm.setRect(0, message.bottom() + 2, WIDTH, 20);
		add(confirm);
		resize(WIDTH, (int) confirm.bottom());
	}

	public static void confirmDeparture(Level level) {
		if (level != null) level.forceDone = true;
	}
}
