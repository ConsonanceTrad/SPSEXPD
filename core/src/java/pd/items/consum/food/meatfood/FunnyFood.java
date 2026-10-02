/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.FunnyBuff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class FunnyFood extends MeatFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FunnyFood.class)
			.t("name", "吮指原味稽")
			.t("desc", "奇怪的料理，会让一切在很长时间里都显得十分滑稽。");
	}



	{
		image = ConsumFoodFoodDict.FUNNY_PARASITE;
		energy = 500f;
	}
	@Override protected void doEat(Hero hero) {
		Buff.prolong(hero, FunnyBuff.class, 1600f);
	}
	@Override public int value() { return quantity; }
}
