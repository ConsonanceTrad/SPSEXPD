/*
 * 敏锐 —— 提高基础感知（搜寻隐藏门与陷阱）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Keen extends Perk {

	static {
		InlineText.of(Keen.class)
				.t("title", "敏锐")
				.t("desc", "提升 %s%% 的基础感知，更容易发现隐藏门与陷阱。");
	}

	public Keen() {
		super(3);
	}

	@Override
	public int image() {
		return PerkImageSheet.KEEN;
	}

	public float baseAwareness() {
		return level() * 0.1f + 0.1f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(baseAwareness() * 100)));
	}
}
