/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.effects.Speck;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Vialupdater extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Vialupdater.class)
			.t("name", "露珠强化器")
			.t("ac_use", "使用")
			.t("desc", "扩容，然后解锁露珠瓶的最终能力。");
	}

	public static final String AC_USE = "USE";
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = false;
		unique = true;
		defaultAction = AC_USE;
	}
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (!AC_USE.equals(action)) {
			super.execute(hero, action);
			return;
		}
		curUser = hero;
		detach(hero.belongings.backpack);
		Dungeon.dewWater = true;
		Dungeon.wings = true;
		if (hero.sprite != null) hero.sprite.centerEmitter().start(Speck.factory(Speck.UP), 0.05f, 10);
		hero.spendAndNext(1f);
		hero.busy();
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
