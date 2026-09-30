/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;

/** Temporarily increases the hero's legacy magic-skill stat. */
public class SuperArcane extends FlavourBuff {

	public static final float DURATION = 30f;
	private static final String LEVEL = "level";

	private int level;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public SuperArcane level(int value) {
		level = Math.max(level, value);
		return this;
	}

	public int level() {
		return level;
	}

	@Override
	public int icon() {
		return BuffIndicator.WAND;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Math.max(5, level), dispTurns());
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
