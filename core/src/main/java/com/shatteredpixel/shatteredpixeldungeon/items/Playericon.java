/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

/** The original completion souvenir from the unfinished boss rush. */
public class Playericon extends Item {
	{
		image = ItemSpriteSheet.PLAYER_ICON;
		stackable = true;
	}
	@Override public boolean doPickUp(Hero hero, int pos) {
		GLog.p(Messages.get(this, "thank4play"));
		return super.doPickUp(hero, pos);
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
