/*
 * 戒指强化 —— 来自破碎 ENHANCED_RINGS（类名避开既有 buff EnhancedRings）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class RingEmpower extends Perk {

	static {
		InlineText.of(RingEmpower.class)
				.t("title", "戒指强化")
				.t("desc", "使用神器后的 %d 回合内，你佩戴的所有戒指提升 1 级。");
	}

	public RingEmpower() {
		super(2);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.ENHANCED_RINGS;
	}

	public float duration() {
		return 3f * level();
	}

	public int ringBonus() {
		return 1;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(duration()));
	}
}
