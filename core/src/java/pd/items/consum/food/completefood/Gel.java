/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class Gel extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Gel.class)
			.t("name", "凝胶")
			.t("desc", "一团凝胶。\n使用_1份原石、1份水_炼金。");
	}



	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0000FF);
	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 10f; }
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
	@Override public int value() { return 50 * quantity; }
}
