/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.equipment.rings.Ring;

/** Original SPS auto-potion; its legacy AutoHealPotion buff contains no active logic. */
public class AutoPotion extends Ring {
	public AutoPotion() {
		anonymize();
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	protected RingBuff buff() {
		return new AutoHealPotion();
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 500 * quantity;
	}

	public class AutoHealPotion extends RingBuff {
	}
}
