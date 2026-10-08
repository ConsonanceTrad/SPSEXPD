/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food;

import pd.atlas.items.ConsumFoodFoodDict;

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




	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 150;
	}

	@Override
	public int value() {
		return 2 * quantity();
	}
}
