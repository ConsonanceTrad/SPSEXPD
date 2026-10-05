/*
 * 近身施法 —— 法杖伤害随距离提升，贴身时获得暴击加成。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class CloseZap extends Perk {

	static {
		InlineText.of(CloseZap.class)
				.t("title", "近身施法")
				.t("desc", "距离目标越近，法杖造成的伤害越高；贴身施法时还会额外获得暴击几率。");
	}

	public CloseZap() {
		super(1);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.CLOSE_ZAP;
	}

	/** 距离 <= 该值时开始加成 */
	public int range() {
		return 4;
	}

	public float damageMultiplier(int distance) {
		if (distance > range()) return 1f;
		return 1f + 0.1f * (range() - distance + 1);
	}
}
