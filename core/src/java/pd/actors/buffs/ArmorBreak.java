package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** SPS-PD's percentage-based incoming damage vulnerability. */
public class ArmorBreak extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ArmorBreak.class)
			.t("name", "护甲破坏")
			.t("desc", "受到的所有伤害增加_%2$d%%_。\n\n剩余时间：%1$s回合。");
	}




	private static final String LEVEL = "level";
	private static final String LEGACY_LEFT = "left";
	private int level;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	public ArmorBreak level(int value) {
		level = Math.max(level, value);
		return this;
	}

	public void set(float duration) { postpone(Math.max(0, duration)); }

	public int level() {
		return level;
	}

	@Override
	public int icon() {
		return BuffIndicator.VULNERABLE;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(), level());
	}

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
