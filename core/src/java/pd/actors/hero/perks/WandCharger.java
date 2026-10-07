/*
 * 法杖充能 —— 法杖恢复充能更快。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class WandCharger extends Perk {

	static {
		InlineText.of(WandCharger.class)
				.t("title", "法杖充能")
				.t("desc", "法杖的充能速度提高 %s%%。");
	}

	public WandCharger() {
		super(3);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.WAND_CHARGE;
	}

	public float factor() {
		return 1f + 0.15f * level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round((factor() - 1f) * 100)));
	}
}
