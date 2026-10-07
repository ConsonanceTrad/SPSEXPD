/*
 * 法杖感知 —— 施法时鉴定法杖。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class WandPerception extends Perk {

	static {
		InlineText.of(WandPerception.class)
				.t("title", "法杖感知")
				.t("desc", "1 级：施法时能初步辨识法杖。\n2 级：施法时能完全辨识法杖。");
	}

	public WandPerception() {
		super(2);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.WAND_PERCEPTION;
	}

	public boolean fullIdentify() {
		return level() >= 2;
	}
}
