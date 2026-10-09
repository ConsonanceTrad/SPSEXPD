/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.messages.InlineText;

/**
 * SPSEXPD: 鲜草 + 枯枝搓成的火种。
 * 目前只是材料本身（用途后续接入炼金体系），面板上不做任何效果。
 */
public class Tinder extends Item {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Tinder.class)
			.t("name", "火种")
			.t("desc", "用鲜草裹着枯枝搓成的引火物。本身烧不出多大动静，却是后续炼金要用到的材料。");
	}

	{
		image = SpecificPlaceHolderDict.SPS_PH_ALCHEMY;
		stackable = true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 5 * quantity; }
}
