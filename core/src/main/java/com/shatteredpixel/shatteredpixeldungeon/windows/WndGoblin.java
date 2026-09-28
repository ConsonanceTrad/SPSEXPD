/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.block.GoblinShield;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

/** Goblin Player's legacy 3,000-gold shield shop. */
public class WndGoblin extends Window {
	private static final int WIDTH = 120;
	public static final int PRICE = 3000;

	public WndGoblin() {
		GoblinShield reward = new GoblinShield();
		IconTitle title = new IconTitle(new ItemSprite(reward.image(), null), Messages.titleCase(reward.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);
		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + 2);
		add(message);
		RedButton buy = new RedButton(Messages.get(this, "buy")) {
			@Override protected void onClick() {
				if (!purchase()) GLog.w(Messages.get(WndGoblin.class, "more_gold"));
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
		GoblinShield reward = new GoblinShield();
		if (!reward.collect(Dungeon.hero.belongings.backpack) && Dungeon.level != null) {
			Heap heap = Dungeon.level.drop(reward, Dungeon.hero.pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
		return true;
	}
}
