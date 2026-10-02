/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.equipment.rings.Ring;
import pd.messages.InlineText;

/** The old three-slot luck charm, represented in the modern misc equipment slot. */
public class FourClover extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FourClover.class)
			.t("name", "四叶薄荷项链")
			.t("desc", "这个四叶草形状的项链能提升佩戴者升级时的增益，并强化附魔装备的效果。");
	}



	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		buffClass = FourCloverBless.class;
		anonymous = true;
	}
	@Override protected RingBuff buff() { return new FourCloverBless(); }
	public class FourCloverBless extends RingBuff { }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public boolean isKnown() { return true; }
	@Override public int value() { return 500 * quantity; }
}
