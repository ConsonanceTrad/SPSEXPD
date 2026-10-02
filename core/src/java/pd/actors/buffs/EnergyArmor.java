/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** Persistent all-source shield used by SPS Hybrid. */
public class EnergyArmor extends ShieldBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(EnergyArmor.class)
			.t("name", "能量护盾")
			.t("desc", "能量护盾会吸收所有类型的伤害。剩余护盾：%s。");
	}

	private static final String LEGACY_LEVEL = "level";
	private static final String SHIELDING = "shielding";
	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public EnergyArmor level(int value) {
		setShield(value);
		return this;
	}

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEGACY_LEVEL, shielding());
	}

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (!bundle.contains(SHIELDING) && bundle.contains(LEGACY_LEVEL)) {
			setShield(Math.max(0, bundle.getInt(LEGACY_LEVEL)));
		}
	}

	@Override public boolean act() { spend(TICK); return true; }
	@Override public int icon() { return BuffIndicator.ARMOR; }
	@Override public String iconTextDisplay() { return Integer.toString(shielding()); }
	@Override public String desc() { return Messages.get(this, "desc", shielding()); }
}
