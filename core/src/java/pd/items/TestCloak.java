/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Levitation;
import pd.actors.hero.Hero;

import java.util.ArrayList;
import pd.messages.InlineText;

/** SaidbySun's single-use experimental cloak. */
public class TestCloak extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TestCloak.class)
			.t("name", "实验斗篷")
			.t("ac_use", "使用")
			.t("desc", "一件阳说制作的斗篷，一次性使用。");
	}




	public static final String AC_USE = "USE";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
		defaultAction = AC_USE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_USE.equals(action)) {
			Buff.affect(hero, HasteBuff.class, 100f);
			Buff.affect(hero, Levitation.class, 100f);
			Buff.affect(hero, Invisibility.class, 100f);
			detach(hero.belongings.backpack);
		} else {
			super.execute(hero, action);
		}
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 50 * quantity; }
}
