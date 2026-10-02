/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** Persistent shield which only absorbs damage whose source is another character. */
public class ShieldArmor extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ShieldArmor.class)
			.t("name", "物理护盾")
			.t("desc", "物理护盾会吸收由其他角色直接造成的伤害。剩余护盾：%s。");
	}



	private static final String LEVEL = "level";
	private int level;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public ShieldArmor level(int value) {
		level = Math.max(level, value);
		return this;
	}

	public int absorb(int damage) {
		int absorbed = Math.min(level, damage);
		level -= absorbed;
		if (level <= 0) detach();
		return damage - absorbed;
	}

	public int level() { return level; }
	@Override public boolean act() { spend(TICK); return true; }
	@Override public int icon() { return BuffIndicator.ARMOR; }
	@Override public String iconTextDisplay() { return Integer.toString(level); }
	@Override public String desc() { return Messages.get(this, "desc", level); }

	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(LEVEL, level); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); level = bundle.getInt(LEVEL); }
}
