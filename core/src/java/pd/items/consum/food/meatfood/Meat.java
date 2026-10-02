/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.hero.Hero;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Meat extends MeatFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Meat.class)
			.t("name", "生肉")
			.t("desc", "集市里常见的肉，最好加工后再吃。");
	}



	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 100f;
	}
	@Override protected void doEat(Hero hero) {
		if (Random.Int(15) == 0) Buff.affect(hero, Poison.class).set(hero.HT / 5f);
	}
	@Override public int value() { return 2 * quantity; }
}
