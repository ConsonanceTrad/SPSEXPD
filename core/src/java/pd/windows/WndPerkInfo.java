/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 特质说明窗：标题带等级，描述按等级着色（升级后才解锁的效果为灰字，
 * 升级后会变化的数值为绿字）。
 */

package pd.windows;

import pd.actors.hero.perks.Perk;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;

public class WndPerkInfo extends Window {

	static {
		InlineText.of(WndPerkInfo.class)
				.t("level", "%d 级")
				.t("level_max", "%1$d/%2$d 级");
	}

	private static final int WIDTH_MIN = 120;
	private static final int WIDTH_MAX = 220;
	private static final int MARGIN = 4;

	public WndPerkInfo(Perk perk) {

		super();

		int width = WIDTH_MIN;

		RenderedTextBlock title = PixelScene.renderTextBlock(
				perk.title() + " " + levelText(perk), 9);
		title.hardlight(Window.TITLE_COLOR);
		title.maxWidth(width - MARGIN * 2);
		title.setPos(MARGIN, MARGIN);
		add(title);

		RenderedTextBlock info = PixelScene.renderTextBlock("", 6);
		info.spans(perk.describeRich(), width - MARGIN * 2);
		info.setPos(MARGIN, title.bottom() + 2);
		add(info);

		while (PixelScene.landscape()
				&& info.height() > 120
				&& width < WIDTH_MAX) {
			width += 20;
			title.maxWidth(width - MARGIN * 2);
			info.spans(perk.describeRich(), width - MARGIN * 2);
		}

		int w = (int) Math.max(info.width(), title.width()) + MARGIN * 2;
		int h = (int) info.bottom() + MARGIN;

		resize(w, h);
	}

	/** 「2 级」或（可升级特质）「2/3 级」 */
	private static String levelText(Perk perk) {
		if (perk.maxLevel() > 1) {
			return Messages.get(WndPerkInfo.class, "level_max", perk.level(), perk.maxLevel());
		}
		return Messages.get(WndPerkInfo.class, "level", perk.level());
	}
}
