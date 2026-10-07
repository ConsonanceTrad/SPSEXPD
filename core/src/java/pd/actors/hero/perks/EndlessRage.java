/*
 * 洪荒之怒 —— 来自破碎 ENDLESS_RAGE。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class EndlessRage extends Perk {

	static {
		InlineText.of(EndlessRage.class)
				.t("title", "洪荒之怒")
				.t("desc", "怒气上限提升至 %s%%。");
	}

	public EndlessRage() {
		super(3);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.ENDLESS_RAGE;
	}

	public float rageCapMultiplier() {
		return 1f + 0.165f * level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(rageCapMultiplier() * 100)));
	}
}
