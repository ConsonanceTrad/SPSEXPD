/*
 * 复春步伐 —— 来自破碎 REJUVENATING_STEPS。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class RejuvenatingSteps extends Perk {

	static {
		InlineText.of(RejuvenatingSteps.class)
				.t("title", "复春步伐")
				.t("desc", "踏上矮草或余烬时，它们会复生为高草并被你踩踏（冷却 %s 回合）。");
	}

	public RejuvenatingSteps() {
		super(2);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.REJUVENATING_STEPS;
	}

	public float cooldown() {
		return 10f - 3f * (level() - 1);
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(cooldown())));
	}
}
