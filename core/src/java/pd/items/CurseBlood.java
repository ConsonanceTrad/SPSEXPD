/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;

import java.util.ArrayList;
import pd.messages.InlineText;

/** One of the original Shadow Eater crafting materials. */
public class CurseBlood extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CurseBlood.class)
			.t("name", "诅咒之液")
			.t("desc", "一瓶被诅咒的浑浊液体。暗噬1/3。");
	}



	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
