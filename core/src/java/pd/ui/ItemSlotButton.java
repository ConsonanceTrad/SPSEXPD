/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 开局物品格子：图标 + 右上角数量角标，点击查看物品详情。
 * 与 PerkSlot（特质格子）同款排版，用于选角详情页的「开局携带物品」分组展示。
 */

package pd.ui;

import pd.ShatteredPixelDungeon;
import pd.items.Item;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.windows.WndInfoItem;
import render.noosa.Game;

public class ItemSlotButton extends Button {

	public static final int BTN = 20;

	public final Item item;

	private final ItemSprite icon;
	private final RenderedTextBlock count;

	public ItemSlotButton(Item item) {
		super();
		this.item = item;

		icon = new ItemSprite(item);
		add(icon);

		int q = (item == null) ? 1 : item.quantity();
		//SPSEXPD: 数量 >1 才显示角标（与特质格子的等级角标同位置）
		count = PixelScene.renderTextBlock(q > 1 ? String.valueOf(q) : "", 6);
		count.hardlight(Window.TITLE_COLOR);
		add(count);

		setSize(BTN, BTN);
	}

	@Override
	protected void layout() {
		super.layout();
		icon.x = x + (width - icon.width()) / 2f;
		icon.y = y + (height - icon.height()) / 2f;
		//SPSEXPD: 角标左移下移一点，使其保持在 16x16 的图标内（格子 20x20、图标居中）
		count.setPos(x + width - count.width() - 3, y + 3);
	}

	@Override
	protected void onClick() {
		super.onClick();
		if (item == null) return;
		//SPSEXPD: 选角界面没有 GameScene，此时直接挂在当前场景上（否则会 NPE）
		WndInfoItem w = new WndInfoItem(item);
		if (Game.scene() instanceof GameScene) {
			GameScene.show(w);
		} else {
			ShatteredPixelDungeon.scene().addToFront(w);
		}
	}
}
