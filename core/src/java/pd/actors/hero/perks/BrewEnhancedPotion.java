/*
 * 强化酿造 —— 炼金时概率产出强化药剂。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class BrewEnhancedPotion extends Perk {

	static {
		InlineText.of(BrewEnhancedPotion.class)
				.t("title", "强化酿造")
				.t("desc", "炼制药剂时有 %d%% 的几率额外产出强化版药剂。");
	}

	public BrewEnhancedPotion() {
		super(1);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.BREW_ENHANCED;
	}

	public float chance() {
		return 0.15f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(chance() * 100));
	}
}
