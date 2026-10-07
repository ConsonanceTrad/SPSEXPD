/*
 * 伴生强化 —— 来自破碎 TWIN_UPGRADES。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class TwinUpgrades extends Perk {

	static {
		InlineText.of(TwinUpgrades.class)
				.t("title", "伴生强化")
				.t("desc", "双持时，若一把武器的阶数比另一把低出至少 %s 阶，则其等级被加强至与另一把相同。");
	}

	public TwinUpgrades() {
		super(2);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.TWIN_UPGRADES;
	}

	public int tierGap() {
		return Math.max(1, 3 - (level() - 1));
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(tierGap()));
	}
}
