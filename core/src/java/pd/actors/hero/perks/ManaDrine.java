/*
 * 法力汲取 —— 法杖击杀敌人时，为所有法杖回复少量充能。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class ManaDrine extends Perk {

	static {
		InlineText.of(ManaDrine.class)
				.t("title", "法力汲取")
				.t("desc", "用法杖击杀敌人时，你的所有法杖都会回复少量充能。");
	}

	public ManaDrine() {
		super(1);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.MANA_DRINE;
	}

	public float chargeGain() {
		return 0.25f;
	}
}
