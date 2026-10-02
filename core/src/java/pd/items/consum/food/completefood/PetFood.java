/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumUsefulUsefulDict;
import pd.messages.InlineText;


public class PetFood extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PetFood.class)
			.t("name", "宠物口粮")
			.t("desc", "为地牢伙伴准备的基础食物。");
	}

	{
		image = ConsumUsefulUsefulDict.PET_FOOD;
		energy = 10f;
	}
	@Override public int value() { return quantity; }
}
