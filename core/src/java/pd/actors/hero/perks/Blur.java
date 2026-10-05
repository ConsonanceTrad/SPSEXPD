/*
 * 虚影 —— 短时间内连续闪避后免疫一次致死伤害。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Blur extends Perk {

	static {
		InlineText.of(Blur.class)
				.t("title", "虚影")
				.t("desc", "短时间内闪避三次后，能在接下来的一段时间内免疫一次致死伤害。");
	}

	public Blur() {
		super(1);
		addTags(Tag.Evade, Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.BLUR;
	}

	/** 触发所需的连续闪避次数 */
	public int requiredEvasions() {
		return 3;
	}
}
