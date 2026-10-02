/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.SuperArcane;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Frenchfries extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Frenchfries.class)
			.t("name", "薯条")
			.t("desc", "过量淀粉警告。\n使用_1份卷轴、2份坚果_炼金。");
	}

	{ image = ConsumFoodFoodDict.FRENCH_FRIES; energy = 150f; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, ShieldArmor.class).level(hero.HT / 2);
		Buff.affect(hero, Recharging.class, 20f);
		Buff.affect(hero, SuperArcane.class, 40f).level(5);
	}
	@Override public int value() { return 20 * quantity; }
}
