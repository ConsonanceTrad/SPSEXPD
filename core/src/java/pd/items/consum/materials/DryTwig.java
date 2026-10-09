/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.messages.InlineText;

/**
 * SPSEXPD: 踩踏高草的收获——枯枝。
 * 与鲜草凑在一起可以搓出火种（见 SpsAlchemyRecipes 的确定性配方）。
 */
public class DryTwig extends Item {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(DryTwig.class)
			.t("name", "枯枝")
			.t("desc", "干透的细枝，一点就着。和鲜草凑在一起，能搓出可以点火的火种。");
	}

	{
		image = SpecificPlaceHolderDict.SPS_PH_ALCHEMY;
		stackable = true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 3 * quantity; }
}
