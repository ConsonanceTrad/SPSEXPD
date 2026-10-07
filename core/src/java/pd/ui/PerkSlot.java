/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 特质格子：图标 + 右上角等级，点击查看说明。
 */

package pd.ui;

import pd.actors.hero.perks.Perk;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.windows.WndPerkInfo;
import render.noosa.ColorBlock;

public class PerkSlot extends Button {

	public static final int BTN = 20;

	public final Perk perk;

	private final PerkIcon icon;
	private final RenderedTextBlock level;
	private ColorBlock highlight;

	public PerkSlot(Perk perk) {
		super();
		this.perk = perk;

		highlight = new ColorBlock(BTN, BTN, 0x8822AA88);
		add(highlight);

		icon = new PerkIcon(perk);
		add(icon);

		//SPSXPD: 右上角始终显示等级（1 级也显示）
		level = PixelScene.renderTextBlock(String.valueOf(perk.level()), 6);
		level.hardlight(Window.TITLE_COLOR);
		add(level);

		showHighlight(false);
		setSize(BTN, BTN);
	}

	public void showHighlight(boolean value) {
		if (highlight != null) highlight.visible = value;
	}

	@Override
	protected void layout() {
		super.layout();
		if (highlight != null) {
			highlight.x = x;
			highlight.y = y;
			highlight.size(width, height);
		}
		icon.x = x + (width - icon.width()) / 2f;
		icon.y = y + (height - icon.height()) / 2f;
		level.setPos(x + width - level.width() - 1, y + 1);
	}

	@Override
	protected void onClick() {
		super.onClick();
		//SPSXPD: 说明窗带等级，描述按等级着色（旧效果灰 / 变化数值绿）
		GameScene.show(new WndPerkInfo(perk));
	}
}
