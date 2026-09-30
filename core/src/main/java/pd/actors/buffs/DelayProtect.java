/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;

/** Converts into one turn of SPS glass shielding on the following actor tick. */
public class DelayProtect extends Buff {
	@Override
	public boolean attachTo(pd.actors.Char target) {
		if (!super.attachTo(target)) return false;
		if (cooldown() == 0) spend(TICK);
		return true;
	}

	@Override
	public boolean act() {
		Buff.affect(target, GlassShield.class).turns(1);
		detach();
		return true;
	}
	@Override public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
}
