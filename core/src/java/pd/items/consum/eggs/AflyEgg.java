/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.Abi;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;

/** Alfred's whistle, represented by an egg action in the original pet system. */
public class AflyEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AflyEgg.class)
			.t("name", "阿比哨子")
			.t("desc", "召唤阿比。\n使用1个复活十字架和1个不思议饭团在阿飞处合成。");
	}

	{ image = ConsumSummorDict.AFLY_EGG_0; }
	@Override protected LegacyPet hatchling() { return new Abi(); }
	@Override public int value() { return 500 * quantity; }
}
