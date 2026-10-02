/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class FishPetFood extends PetFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FishPetFood.class)
			.t("name", "鱼味宠物口粮")
			.t("desc", "鱼味的伙伴食物，提供的能量远多于普通宠物口粮。");
	}

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 100f;
	}
}
