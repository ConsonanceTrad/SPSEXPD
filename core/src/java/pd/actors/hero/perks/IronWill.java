/*
 * 钢铁意志 —— 来自破碎 IRON_WILL。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class IronWill extends Perk {

	static {
		InlineText.of(IronWill.class)
				.t("title", "钢铁意志")
				.t("desc", "你的纹章所提供的护盾增加 %s 点。");
	}

	public IronWill() {
		super(2);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.IRON_WILL;
	}

	public int shieldBonus() {
		return level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(shieldBonus()));
	}
}
