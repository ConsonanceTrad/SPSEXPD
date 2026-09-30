/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import render.utils.Bundle;

/** Crystalline ice which punishes movement, matching FrostNova's old attack. */
public class StoneIce extends Buff implements Hero.Doom {
	private int lastPos;
	private float left;
	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	@Override public boolean attachTo(Char target) {
		lastPos = target.pos;
		return super.attachTo(target);
	}
	@Override public boolean act() {
		if (!target.isAlive()) {
			detach();
			return true;
		}
		if (target.pos != lastPos) {
			lastPos = target.pos;
			target.damage(Math.min(1000, Math.max(1, target.HT / 20)), this);
		}
		Buff.detach(target, Burning.class);
		Buff.detach(target, Frost.class);
		left -= TICK;
		spend(TICK);
		if (left <= 0) detach();
		return true;
	}
	public StoneIce level(float duration) {
		left = Math.max(left, duration);
		return this;
	}
	@Override public int icon() { return BuffIndicator.FROST; }
	@Override public float iconFadePercent() { return Math.max(0, (8f - left) / 8f); }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns(left)); }
	@Override public void onDeath() {
		Badges.validateDeathFromFire();
		Dungeon.fail(this);
		GLog.n(Messages.get(this, "ondeath"));
	}
	private static final String LAST_POS = "last_pos";
	private static final String LEFT = "left";
	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LAST_POS, lastPos);
		bundle.put(LEFT, left);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		lastPos = bundle.getInt(LAST_POS);
		left = bundle.getFloat(LEFT);
	}
}
