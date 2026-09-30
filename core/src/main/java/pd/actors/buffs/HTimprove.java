/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** Temporarily raises the hero's unmodified maximum HP by 20%. */
public class HTimprove extends FlavourBuff {
	{ type = buffType.NEUTRAL; announced = true; }
	@Override public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		if (target instanceof Hero) ((Hero) target).updateHT(true);
		return true;
	}
	@Override public void detach() {
		Char oldTarget = target;
		super.detach();
		if (oldTarget instanceof Hero) ((Hero) oldTarget).updateHT(false);
	}
	@Override public int icon() { return BuffIndicator.BLESS; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
