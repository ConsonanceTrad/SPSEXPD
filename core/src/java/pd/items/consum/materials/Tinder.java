/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import pd.atlas.items.ConsumGoodsMaterialsMaterialsExtraDict;
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
			.t("desc", "晒干的引火物，是炼金釜认可的唯一火源。炼制药水已经用不上火了——它是预留给烹饪配方的燃料，需要时投进辅材格即可。");
	}

	{
		image = ConsumGoodsMaterialsMaterialsExtraDict.TINDER;
		stackable = true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 5 * quantity; }
}
