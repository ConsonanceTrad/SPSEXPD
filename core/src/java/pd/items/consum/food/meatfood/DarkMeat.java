/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class DarkMeat extends MeatFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DarkMeat.class)
			.t("name", "腐蚀肉")
			.t("desc", "在黑暗中放置很久的肉，吃下后居然能够恢复生命。");
	}

	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { DarkMeat result = new DarkMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		hero.HP = Math.min(hero.HT, hero.HP + hero.HT / 4);
	}
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
	@Override public int value() { return 3 * quantity; }
}
