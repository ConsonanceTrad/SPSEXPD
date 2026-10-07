/*
 * 药剂增效 —— 提升治疗药剂的总量与生效速度。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class EfficientPotionOfHealing extends Perk {

	static {
		InlineText.of(EfficientPotionOfHealing.class)
				.t("title", "药剂增效")
				.t("desc", "你使用的治疗药剂总量提升 %s%%，且生效更快。");
	}

	public EfficientPotionOfHealing() {
		super(3);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.POTION_EFF_HEALING;
	}

	public float bonus() {
		return level() * 0.1f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(bonus() * 100)));
	}
}
