/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;
import pd.messages.InlineText;


public class RiceGruel extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RiceGruel.class)
			.t("name", "米粥")
			.t("desc", "加一份水是饭，加两份水是粥，加三份水我想都不敢想。\n使用_1份主食、2份水_炼制。");
	}




	{
		image = ConsumFoodFoodDict.RICE_GRUEL;
		energy = 250f;
	}

	public RiceGruel() { this(2); }
	public RiceGruel(int number) { quantity = number; }

	@Override public int value() { return 10 * quantity; }
}
