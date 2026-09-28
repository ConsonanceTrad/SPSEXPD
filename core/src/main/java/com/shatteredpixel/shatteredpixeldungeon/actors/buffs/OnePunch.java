/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

/** Stores SeriousPunch's multiplier until the hero's next successful attack. */
public class OnePunch extends Buff {

	private static final String LEVEL = "level";
	private int level;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public OnePunch level(int value) {
		level = Math.max(level, Math.max(0, value));
		return this;
	}

	public int level() {
		return level;
	}

	public int empower(int damage) {
		return Math.max(0, Math.round(damage * (1f + level * 0.1f)));
	}

	@Override
	public int icon() {
		return BuffIndicator.FURY;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", level * 10);
	}

	@Override
	public boolean act() {
		spend(TICK);
		return true;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEVEL, level);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		level = Math.max(0, bundle.getInt(LEVEL));
	}
}
