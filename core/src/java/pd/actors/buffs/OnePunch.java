/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** Stores SeriousPunch's multiplier until the hero's next successful attack. */
public class OnePunch extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(OnePunch.class)
			.t("name", "认真一拳")
			.t("desc", "下一次成功攻击额外造成%d%%伤害。");
	}


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
