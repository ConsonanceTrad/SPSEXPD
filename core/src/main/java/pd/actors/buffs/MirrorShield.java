/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** Link Sword protection: incoming damage is cancelled and partly reflected. */
public class MirrorShield extends FlavourBuff {
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon(){ return BuffIndicator.ARMOR; }
	@Override public String desc(){ return Messages.get(this,"desc",dispTurns()); }
}
