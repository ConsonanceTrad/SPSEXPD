/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

import java.util.ArrayList;

/** One of the original Shadow Eater crafting materials. */
public class CurseBlood extends Item {
	{
		image = ItemSpriteSheet.CURSE_BLOOD;
		stackable = false;
		unique = true;
	}
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
