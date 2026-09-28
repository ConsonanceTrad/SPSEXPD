/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

/** Percentage damage reduction used by SPS legacy effects. */
public class DefenceUp extends FlavourBuff {
	private static final String LEVEL = "level";
	private static final String LEGACY_LEFT = "left";
	private int level;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public DefenceUp level(int value) {
		level = Math.max(level, value);
		return this;
	}

	public void set(float duration) { postpone(Math.max(0, duration)); }

	public int level() { return level; }
	public float damageMultiplier() { return Math.max(0f, 1f - level / 100f); }

	@Override public int icon() { return BuffIndicator.ARMOR; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns(), level); }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEVEL, level);
		bundle.put(LEGACY_LEFT, Math.max(0, cooldown()));
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		level = bundle.getInt(LEVEL);
		if (bundle.contains(LEGACY_LEFT)) postpone(Math.max(0, bundle.getFloat(LEGACY_LEFT)));
	}
}
