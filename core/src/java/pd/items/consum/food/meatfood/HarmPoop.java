/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;

public class HarmPoop extends MeatFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HarmPoop.class)
			.t("name", "有害秽物")
			.t("desc", "勉强可以吃，但会导致中毒和迟缓。");
	}



	{
		image = ConsumGoodsMaterialsMaterialsDict.SCRAP;
		energy = 10f;
		hornValue = 0;
	}
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, Poison.class).set(hero.HT / 10f);
		Buff.prolong(hero, Slow.class, 5f);
	}
	@Override public int value() { return 2 * quantity; }
}
