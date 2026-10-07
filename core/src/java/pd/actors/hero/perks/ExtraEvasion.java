/*
 * 灵动 —— 提高闪避几率。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class ExtraEvasion extends Perk {

	static {
		InlineText.of(ExtraEvasion.class)
				.t("title", "灵动")
				.t("desc", "提供 %s%% 的闪避几率。");
	}

	public ExtraEvasion() {
		super(4);
		addTags(Tag.Evade);
	}

	@Override
	public int image() {
		return PerkImageSheet.DEX_EXTRA;
	}

	public float prob() {
		return level() * 0.075f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(prob() * 100)));
	}
}
