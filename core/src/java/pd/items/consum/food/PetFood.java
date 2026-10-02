package pd.items.consum.food;

import pd.atlas.items.ConsumUsefulUsefulDict;
import pd.messages.InlineText;
public class PetFood extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PetFood.class)
			.t("name", "宠物口粮")
			.t("desc", "为宠物专门设计的食物。\n使用2份坚果和1份水炼金。");
	}

	{ image = ConsumUsefulUsefulDict.PET_FOOD; energy = 10f; }
	@Override public int value() { return quantity; }
}
