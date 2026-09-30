/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.effects.particles.ShadowParticle;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;

public class ShadowCurse extends Buff {
	private static final String TICKS = "ticks";
	private static final String FIRST = "first";
	private int ticks;
	private boolean first = true;
	{ type = buffType.NEGATIVE; announced = true; }

	@Override public boolean act() {
		if (!target.isAlive()) {
			detach();
			return true;
		}
		if (first) {
			first = false;
			target.damage(Math.min(1000, target.HT / 30), this);
		}
		ticks++;
		if (ticks > 3 && target.isAlive()) {
			if (target.sprite != null) target.sprite.emitter().burst(ShadowParticle.CURSE, 6);
			target.damage(Math.round(target.HT / 10f), this);
			detach();
		}
		spend(TICK);
		return true;
	}

	@Override public int icon() { return BuffIndicator.TIME; }
	@Override public String iconTextDisplay() { return Integer.toString(Math.max(0, 4 - ticks)); }
	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TICKS, ticks);
		bundle.put(FIRST, first);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		ticks = Math.max(0, Math.min(4, bundle.getInt(TICKS)));
		first = bundle.contains(FIRST) && bundle.getBoolean(FIRST);
	}
}
