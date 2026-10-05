/*
 * 潜行施法 —— 隐身状态下施法不会暴露自己。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class StealthCaster extends Perk {

	static {
		InlineText.of(StealthCaster.class)
				.t("title", "潜行施法")
				.t("desc", "在隐身状态下施法不会打破你的隐身。");
	}

	public StealthCaster() {
		super(1);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.STEALTH_CASTER;
	}
}
