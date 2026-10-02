/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.sprites.ItemSprite;
import pd.messages.InlineText;

/** Original red-glowing meat handed out by Xavier251998. */
public class FireMeat extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FireMeat.class)
			.t("name", "火焰肉")
			.t("desc", "一块散发着温暖红光的肉。")
			.t("eat_msg", "这块肉真烫！");
	}


	private static final ItemSprite.Glowing RED = new ItemSprite.Glowing(0xCC0000);

	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 150;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return RED;
	}

	@Override
	public int value() {
		return 2 * quantity();
	}
}
