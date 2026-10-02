/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.Dungeon;
import pd.items.Gold;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.utils.GLog;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Millilitre's original blood-for-gold trade. */
public class WndIssic extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndIssic.class)
			.t("message", "测试太可怕了……你能给我一些温暖吗？我会给东西作为回报的。")
			.t("buy", "拥抱……卖血机？");
	}



	private static final int WIDTH = 120;

	public WndIssic() {
		Gold icon = new Gold();
		IconTitle title = new IconTitle(new ItemSprite(icon.image(), null), Messages.titleCase(icon.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);

		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + 2);
		add(message);

		RedButton sell = new RedButton(Messages.get(this, "buy")) {
			@Override protected void onClick() {
				if (canSellBlood(Dungeon.hero.HP)) {
					Dungeon.hero.HP -= 100;
					new Gold(Random.Int(500, 3000)).doPickUp(Dungeon.hero);
				} else {
					GLog.w(Messages.get(WndHotel.class, "more_gold"));
				}
				hide();
			}
		};
		sell.setRect(0, message.bottom() + 2, WIDTH, 20);
		add(sell);
		resize(WIDTH, (int) sell.bottom());
	}

	public static boolean canSellBlood(int hp) {
		return hp > 150;
	}
}
