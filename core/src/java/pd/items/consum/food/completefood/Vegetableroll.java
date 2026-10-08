/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.SuperArcane;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class Vegetableroll extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Vegetableroll.class)
			.t("name", "菜卷")
			.t("desc", "肉卷的兄弟。\n使用_1份卷轴、1份蔬菜_炼金。");
	}



	{ image = ConsumFoodFoodDict.CHICKEN_WRAP; energy = 170f; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 4);
		Buff.affect(hero, Recharging.class, 20f);
		Buff.affect(hero, SuperArcane.class, 40f).level(5);
	}
	@Override public int value() { return 6 * quantity; }
}
