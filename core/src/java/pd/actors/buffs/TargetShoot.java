/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** Legacy aimed-shot state. Missile damage is multiplied in Hero.damageRoll(). */
public class TargetShoot extends FlavourBuff {
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.MARK; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
