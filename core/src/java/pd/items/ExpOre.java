/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificTaskDict;

import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.effects.Speck;

import java.util.ArrayList;
import pd.messages.InlineText;

public class ExpOre extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ExpOre.class)
			.t("name", "经验之石")
			.t("ac_use", "使用")
			.t("desc", "使用后立刻获得足以提升一级的经验，并使宠物等级提高一级。");
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
		hero.earnExp(hero.maxExp(), ExpOre.class);
		hero.petLevel++;
		detach(hero.belongings.backpack);
		hero.sprite.centerEmitter().start(Speck.factory(Speck.UP), 0.05f, 10);
		hero.spendAndNext(Actor.TICK);
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
