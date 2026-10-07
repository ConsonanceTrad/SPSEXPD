/*
 * 博识 —— 拾取装备时概率直接鉴定其属性。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Knowledgeable extends Perk {

	static {
		InlineText.of(Knowledgeable.class)
				.t("title", "博识")
				.t("desc", "拾取装备时有 %s%% 的几率直接鉴定其属性。");
	}

	public Knowledgeable() {
		super(3);
	}

	@Override
	public int image() {
		return PerkImageSheet.KNOWLEDGE;
	}

	public float chancePerLevel() {
		return 0.2f;
	}

	public float chance() {
		return Math.min(1f, level() * chancePerLevel());
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(chance() * 100)));
	}
}
