/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** Increases damage from SPS and Shattered magic sources by 50%. */
public class MagicWeak extends FlavourBuff {
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.VULNERABLE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
