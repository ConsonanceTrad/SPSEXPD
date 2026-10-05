/*
 * 法杖感知 —— 施法时鉴定法杖。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class WandPerception extends Perk {

	static {
		InlineText.of(WandPerception.class)
				.t("title", "法杖感知")
				.t("desc", "施法时能初步辨识法杖；等级更高时可以完全辨识。");
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
