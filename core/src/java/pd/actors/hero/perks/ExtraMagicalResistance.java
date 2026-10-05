/*
 * 魔法抗性 —— 提高魔法抗性。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class ExtraMagicalResistance extends Perk {

	static {
		InlineText.of(ExtraMagicalResistance.class)
				.t("title", "魔法抗性")
				.t("desc", "提供 %d%% 的魔法抗性。");
	}

	public ExtraMagicalResistance() {
		super(3);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.MAGICAL_RESISTANCE;
	}

	public float ratio() {
		return level() * 0.15f + 0.05f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(ratio() * 100));
	}
}
