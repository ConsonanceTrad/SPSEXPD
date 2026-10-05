/*
 * 猎杀步伐 —— 击杀敌人后短暂加速。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class FastMoveOnKilling extends Perk {

	static {
		InlineText.of(FastMoveOnKilling.class)
				.t("title", "猎杀步伐")
				.t("desc", "击杀敌人后，你在接下来的一段时间内移动更快。");
	}

	public FastMoveOnKilling() {
		super(1);
	}

	@Override
	public int image() {
		return PerkImageSheet.FAST_MOVE_ON_KILLING;
	}

	public float speedMultiplier() {
		return 0.85f;
	}

	/** 加速持续回合 */
	public float duration() {
		return 5f;
	}
}
