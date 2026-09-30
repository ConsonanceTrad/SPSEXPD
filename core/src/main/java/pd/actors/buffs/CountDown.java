/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.effects.particles.ShadowParticle;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;

public class CountDown extends Buff implements Hero.Doom, Buff.DOTbuff {
	private static final String TICKS = "ticks";
	private int ticks;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public boolean act() {
		if (target.isAlive() && ++ticks > 5) {
			if (target.sprite != null) target.sprite.emitter().burst(ShadowParticle.CURSE, 6);
			target.damage(Math.max(1, Math.round(target.HT / 4f)), this);
			detach();
		} else spend(TICK);
		return true;
	}
	@Override public int totalIncomingDMG() { return ticks <= 5 ? Math.max(1, Math.round(target.HT / 4f)) : 0; }
	@Override public int icon() { return BuffIndicator.TIME; }
	@Override public String iconTextDisplay() { return Integer.toString(Math.max(0, 6 - ticks)); }
	@Override public String desc() { return Messages.get(this, "desc", Math.max(0, 6 - ticks)); }
	@Override public void onDeath() { Dungeon.fail(this); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(TICKS, ticks); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); ticks = bundle.getInt(TICKS); }
}
