/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.ui.BuffIndicator;

/** Alfred's original temporary blessing: +1 strength and +6% base loot chance. */
public class AflyBless extends FlavourBuff {
	{
		type = buffType.POSITIVE;
		announced = true;
	}
	@Override public int icon() { return BuffIndicator.BLESS; }
}
