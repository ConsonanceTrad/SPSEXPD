/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class Fruitsalad extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Fruitsalad.class)
			.t("name", "水果沙拉")
			.t("desc", "事实上制作这种食物并不需要沙拉。\n使用_2份水果、1份水_炼金。");
	}



	{ image = ConsumFoodFoodDict.FRUIT_SMOOTHIE; energy = 130f; }
	@Override protected void doEat(Hero hero) {
		heal(hero, hero.HT / 3);
		Buff.affect(hero, BerryRegeneration.class).level(Math.max(hero.HT / 2, 30));
	}
	@Override public int value() { return 2 * quantity; }
}
