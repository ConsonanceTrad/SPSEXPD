/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.effects.particles.FlameParticle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class GoldBag extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GoldBag.class)
			.t("name", "一袋金币")
			.t("ac_use", "提现")
			.t("desc", "装有10000枚金币的袋子。");
	}

	public static final String AC_USE = "USE";
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
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
		use(hero);
	}

	public boolean use(Hero hero) {
		if (hero == null || hero.belongings == null || !hero.belongings.contains(this)) return false;
		detach(hero.belongings.backpack);
		Dungeon.gold += 10000;
		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			hero.sprite.centerEmitter().start(FlameParticle.FACTORY, 0.2f, 3);
		}
		hero.spendAndNext(1f);
		return true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 10000 * quantity; }
}
