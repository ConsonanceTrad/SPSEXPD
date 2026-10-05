/*
 * 从容 —— 踩到隐藏陷阱时有一定几率只揭示而不触发。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Ease extends Perk {

	static {
		InlineText.of(Ease.class)
				.t("title", "从容")
				.t("desc", "踩中隐藏陷阱时有 %d%% 的几率只将其揭示而不触发。");
	}

	public Ease() {
		super(1);
	}

	@Override
	public int image() {
		return PerkImageSheet.EASE;
	}

	public float chance() {
		return 0.5f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(chance() * 100));
	}
}
