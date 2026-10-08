/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.messages.InlineText;
import pd.atlas.items.EquipmentNonEquipDict;

public class Gel extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Gel.class)
			.t("name", "凝胶")
			.t("desc", "一团凝胶。\n使用_1份原石、1份水_炼金。");
	}



	{ image = EquipmentNonEquipDict.YELLOW_UPGRADE_BLOB; energy = 10f; }
	@Override public int value() { return 50 * quantity; }
}
