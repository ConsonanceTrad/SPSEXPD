/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import pd.messages.InlineText;

public class EarthMeat extends MeatFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EarthMeat.class)
			.t("name", "腌制扣肉")
			.t("desc", "利用大地力量腌制的肉块，充满了自然的庇护。");
	}



	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { EarthMeat result = new EarthMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, Barkskin.class).set(hero.lvl * 3, 1);
	}
	@Override public int value() { return 3 * quantity; }
}
