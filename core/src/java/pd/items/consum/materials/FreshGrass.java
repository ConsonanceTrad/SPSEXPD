/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.messages.InlineText;

/**
 * SPSEXPD: 踩踏高草的收获——鲜草。
 * 与枯枝凑在一起可以搓出火种（见 SpsAlchemyRecipes 的确定性配方）。
 */
public class FreshGrass extends Item {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(FreshGrass.class)
			.t("name", "鲜草")
			.t("desc", "刚踩下来的、还带着水汽的鲜草。和枯枝凑在一起，能搓出可以点火的火种。");
	}

	{
		image = SpecificPlaceHolderDict.SPS_PH_ALCHEMY;
		stackable = true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 3 * quantity; }
}
