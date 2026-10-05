/*
 * 迅疾配装 —— 来自破碎 SWIFT_EQUIP。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class SwiftEquip extends Perk {

	static {
		InlineText.of(SwiftEquip.class)
				.t("title", "迅疾配装")
				.t("desc", "每 %d 回合可以瞬时更换一次装备的武器。");
	}

	public SwiftEquip() {
		super(2);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.SWIFT_EQUIP;
	}

	public int cooldownTurns() {
		return Math.max(10, 20 - 10 * (level() - 1));
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", cooldownTurns());
	}
}
