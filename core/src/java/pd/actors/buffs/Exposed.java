/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.InlineText;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 决斗刺剑造成的「破绽百出」。
 *
 * <p>记下攻击者曾经从哪些方位打过这个单位（只看方向、不看距离，所以相邻格与更远的同一射线算同一方位）。
 * 攒够需要的方位数后，刺剑会当场斩杀。</p>
 */
public class Exposed extends Buff {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Exposed.class)
			.t("name", "破绽百出")
			.t("desc", "已被从 _%1$d_ 个方位挑开破绽（共需 _%2$d_ 个）。攒满后，刺剑的下一击会当场斩杀。");
	}

	private static final String DIRECTIONS = "directions";
	private static final String REQUIRED = "required";

	/** 方位位掩码，(dy+1)*3+(dx+1) 的第几位。 */
	private int directions = 0;
	private int required = 8;

	{
		type = buffType.NEGATIVE;
	}

	/** SPSEXPD: 破绽必须在敌方详情页/状态栏可见——默认 icon() 是 NONE，不覆写就完全不显示。 */
	@Override
	public int icon() {
		return BuffIndicator.TARGETED;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", count(), required);
	}

	public Exposed setup(int required) {
		this.required = Math.max(1, required);
		return this;
	}

	/** 标记一个方位（0..8，其中 4 是自身所在格，不记）；返回是否为新方位。 */
	public boolean mark(int dir) {
		if (dir < 0 || dir > 8 || dir == 4) return false;
		int bit = 1 << dir;
		if ((directions & bit) != 0) return false;
		directions |= bit;
		return true;
	}

	public int count() {
		return Integer.bitCount(directions);
	}

	public int required() {
		return required;
	}

	public boolean complete() {
		return count() >= required;
	}

	public void clearMarks() {
		directions = 0;
	}

	@Override
	public String iconTextDisplay() {
		return count() + "/" + required;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DIRECTIONS, directions);
		bundle.put(REQUIRED, required);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		directions = bundle.getInt(DIRECTIONS);
		required = bundle.getInt(REQUIRED);
	}
}
