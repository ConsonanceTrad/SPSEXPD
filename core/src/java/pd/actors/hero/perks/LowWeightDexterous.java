/*
 * 轻装上阵 —— 护甲力量需求低于自身力量时提高闪避。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class LowWeightDexterous extends Perk {

	static {
		InlineText.of(LowWeightDexterous.class)
				.t("title", "轻装上阵")
				.t("desc", "当你的力量超过当前护甲需求时，获得额外闪避几率。");
	}

	public LowWeightDexterous() {
		super(1);
		addTags(Tag.Evade);
	}

	@Override
	public int image() {
		return PerkImageSheet.LOW_WEIGHT_DEX;
	}

	/** 力量溢出每点的闪避加成 */
	public float evasionPerPoint() {
		return 0.05f;
	}
}
