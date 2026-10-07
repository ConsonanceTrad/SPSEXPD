/*
 * 奥术增幅 —— 法杖伤害更高。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class WandArcane extends Perk {

	static {
		InlineText.of(WandArcane.class)
				.t("title", "奥术增幅")
				.t("desc", "法杖造成的伤害提高 %s%%。");
	}

	public WandArcane() {
		super(3);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.WAND_ARCANE;
	}

	public float bonus() {
		return level() * 0.1f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(bonus() * 100)));
	}
}
