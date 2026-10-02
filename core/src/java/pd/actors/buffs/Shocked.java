/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** SPS shock: delayed percentage damage with a bounded saved duration. */
public class Shocked extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Shocked.class)
			.t("name", "电击")
			.t("desc", "电击首次发作时会造成基于生命上限的伤害。");
	}



	private static final String LEFT = "left";
	private static final String FIRST = "first";
	private float left;
	private boolean first = true;

	{ type = buffType.NEGATIVE; announced = true; }

	public Shocked level(int value) { left = Math.max(left, value); return this; }
	public Shocked set(float duration) { left = Math.max(left, duration); return this; }

	@Override public boolean act() {
		if (!target.isAlive() || left <= 0) {
			detach();
			return true;
		}
		if (first) {
			first = false;
			target.damage(Math.min(1000, target.HT / 30), this);
		}
		left -= TICK;
		spend(TICK);
		if (left <= 0) detach();
		return true;
	}

	@Override public int icon() { return BuffIndicator.RECHARGING; }
	@Override public String iconTextDisplay() { return Integer.toString(Math.max(0, (int)Math.ceil(left))); }
	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
		bundle.put(FIRST, first);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = Math.max(0, bundle.getFloat(LEFT));
		first = bundle.contains(FIRST) && bundle.getBoolean(FIRST);
	}
}
