/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.rings.Ring;

/** The old three-slot luck charm, represented in the modern misc equipment slot. */
public class FourClover extends Ring {
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
