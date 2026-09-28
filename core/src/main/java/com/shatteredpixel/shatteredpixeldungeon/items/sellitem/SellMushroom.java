/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Pill;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class SellMushroom extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM; }
	@Override protected void onUse(Hero hero) { GLog.w(Messages.get(this, "no")); }
	@Override public int value() { return 100 * quantity; }
}
