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
import pd.messages.InlineText;

/** XixiZero's original two-answer Egoal dialog. */
public class WndEgoalInfo extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndEgoalInfo.class)
			.t("title", "西西的测试")
			.t("info1", "喵呜，你好啊，冒险者。这里真是一个平静安宁的地方，你也这么觉得对吧？你是我见过的最阳光的冒险者，因为本喵没有感觉到你心头的压力。\n\n喵呜，那么问题来了：\n_Egoal_是不是个很不错的家伙呢？")
			.t("yes", "当然")
			.t("tell1", "你的回答真令本喵很高兴，喵呜~不过这次我没有礼物给你喵。祝你好运。")
			.t("no", "他是谁？")
			.t("tell2", "喵呜，你听说过_黑暗的像素地牢_吗？如果有机会，希望你也能去那里继续你的冒险，喵，你会去的对吧，以一个冒险者的身份。");
	}

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
