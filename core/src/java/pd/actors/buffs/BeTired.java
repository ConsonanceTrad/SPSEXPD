/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** SPS fatigue: taking sixteen hits before it expires triggers backlash damage. */
public class BeTired extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BeTired.class)
			.t("name", "疲劳")
			.t("desc", "每次受到攻击都会累积一层疲劳。在状态结束前累积十六层时，会受到最大生命值10%%的伤害。\n\n剩余回合：%1$s。还需受击：%2$d。");
	}


	private static final String LEVEL = "level";
	private static final String LEFT = "left";
	private static final int TRIGGER_HITS = 16;

	private int level;
	private float left;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	public BeTired set(float duration) {
		left = Math.max(left, duration);
		return this;
	}

	public int level() {
		return level;
	}

	public float left() {
		return left;
	}

	public void recordHit() {
		level++;
	}

	@Override
	public boolean act() {
		spend(TICK);
		left -= TICK;
		if (level >= TRIGGER_HITS) {
			int backlash = target.HT / 10;
			detach();
			target.damage(backlash, this);
		} else if (left <= 0) {
			detach();
		}
		return true;
	}

	@Override public int icon() { return BuffIndicator.LOCKED_FLOOR; }
	@Override public String iconTextDisplay() { return Integer.toString(Math.max(0, TRIGGER_HITS - level)); }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns(left), Math.max(0, TRIGGER_HITS - level)); }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEVEL, level);
		bundle.put(LEFT, left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		level = Math.max(0, bundle.getInt(LEVEL));
		left = Math.max(0, bundle.getFloat(LEFT));
	}
}
