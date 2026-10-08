/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import pd.messages.InlineText;

public class DarkMeat extends MeatFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DarkMeat.class)
			.t("name", "腐蚀肉")
			.t("desc", "在黑暗中放置很久的肉，吃下后居然能够恢复生命。");
	}



	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { DarkMeat result = new DarkMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		if (!pd.actors.hero.perks.BloodShield.convert(hero, hero.HT / 4)) hero.HP = Math.min(hero.HT, hero.HP + hero.HT / 4);
	}
	@Override public int value() { return 3 * quantity; }
}
