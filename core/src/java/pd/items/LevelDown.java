/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificTaskDict;

import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.effects.Speck;

import java.util.ArrayList;
import pd.messages.InlineText;

public class LevelDown extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LevelDown.class)
			.t("name", "成长核心")
			.t("ac_use", "使用")
			.t("desc", "使用后立刻降低一级，但不会使等级低于1级。");
	}


	public static final String AC_USE = "USE";
	{
		image = SpecificTaskDict.ORE_0;
		stackable = true;
		defaultAction = AC_USE;
	}

	@Override public ArrayList<String> actions(Hero hero) { ArrayList<String> actions = super.actions(hero); actions.add(AC_USE); return actions; }
	@Override public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_USE.equals(action)) return;
		reduceLevel(hero);
		detach(hero.belongings.backpack);
		hero.sprite.centerEmitter().start(Speck.factory(Speck.DOWN), 0.05f, 10);
		hero.spendAndNext(Actor.TICK);
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }

	public static boolean reduceLevel(Hero hero) {
		if (hero.lvl <= 1) return false;
		hero.lvl--;
		hero.exp = Math.min(hero.exp, hero.maxExp() - 1);
		hero.updateHT(false);
		return true;
	}
}
