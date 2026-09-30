/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** Triples attacks against full-health mobs and adds 50% against critically wounded mobs. */
public class SpAttack extends FlavourBuff {
	{ type = buffType.NEUTRAL; announced = true; }
	@Override public int icon() { return BuffIndicator.WEAPON; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
