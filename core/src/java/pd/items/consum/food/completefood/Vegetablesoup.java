/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Vegetablesoup extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Vegetablesoup.class)
			.t("name", "菜汤")
			.t("desc", "把杂七杂八的蔬菜放到一起煮出来的汤。\n使用_1份水、2份蔬菜_炼金。");
	}

	{ image = ConsumFoodFoodDict.VEGETABLE_SOUP; energy = 90f; }
	@Override protected void doEat(Hero hero) {
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 2);
	}
	@Override public int value() { return quantity; }
}
