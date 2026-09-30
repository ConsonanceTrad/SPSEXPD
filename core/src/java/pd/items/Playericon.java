/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;

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
