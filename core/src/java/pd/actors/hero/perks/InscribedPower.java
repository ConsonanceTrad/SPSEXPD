/*
 * 卷藏秘能 —— 来自破碎 INSCRIBED_POWER。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class InscribedPower extends Perk {

	static {
		InlineText.of(InscribedPower.class)
				.t("title", "卷藏秘能")
				.t("desc", "阅读卷轴或使用法术结晶后，接下来 %d 次施法获得 %d 级额外等级。");
	}

	public InscribedPower() {
		super(2);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.INSCRIBED_POWER;
	}

	public int charges() {
		return 2 + level();
	}

	public int castLevelBonus() {
		return 1 + level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", charges(), castLevelBonus());
	}
}
