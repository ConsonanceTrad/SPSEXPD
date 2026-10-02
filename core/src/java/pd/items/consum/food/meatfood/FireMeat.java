/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.items.consum.food.Food;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class FireMeat extends MeatFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FireMeat.class)
			.t("name", "烤肉排")
			.t("desc", "烧烤是最基础的处理方式，可以延长保存时间并提供更多能量。");
	}



	private static final ItemSprite.Glowing RED = new ItemSprite.Glowing(0xCC0000);
	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 150f;
	}
	public static Food cook(int quantity) { FireMeat result = new FireMeat(); result.quantity(quantity); return result; }
	@Override public ItemSprite.Glowing glowing() { return RED; }
	@Override public int value() { return 2 * quantity; }
}
