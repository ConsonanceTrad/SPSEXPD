package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import watabou.utils.Bundle;

public class BerryRegeneration extends Buff {

	private static final String REGEN_LEFT = "regen_left";
	private static final String LEGACY_REGEN_LEFT = "regenleft";
	private int regenLeft;

	public int level() {
		return regenLeft;
	}

	public void level(int value) {
		regenLeft = Math.max(regenLeft, value);
	}

	@Override
	public boolean act() {
		if (!target.isAlive() || regenLeft <= 0) {
			detach();
			return true;
		}
		if (target.HP < target.HT) {
			target.HP = Math.min(target.HT, target.HP + 1 + target.HT / 25);
		}
		regenLeft--;
		spend(TICK);
		if (regenLeft <= 0) detach();
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.HEALING;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", regenLeft);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(REGEN_LEFT, regenLeft);
		bundle.put(LEGACY_REGEN_LEFT, regenLeft);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		regenLeft = bundle.contains(REGEN_LEFT)
				? bundle.getInt(REGEN_LEFT)
				: bundle.getInt(LEGACY_REGEN_LEFT);
	}
}
