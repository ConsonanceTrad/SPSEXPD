/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.actbuff;

import pd.actors.buffs.FlavourBuff;
import pd.ui.BuffIndicator;

public class Nmstop extends FlavourBuff {
	public static final float DURATION = 10f;
	{ type = buffType.NEUTRAL; }
	@Override public int icon() { return BuffIndicator.FROST; }
}
