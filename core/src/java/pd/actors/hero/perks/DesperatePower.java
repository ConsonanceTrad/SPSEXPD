/*
 * 绝境迫能 —— 来自破碎 DESPERATE_POWER。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class DesperatePower extends Perk {

	static {
		InlineText.of(DesperatePower.class)
				.t("title", "绝境迫能")
				.t("desc", "使用魔杖最后一点充能施法时，效果获得 %d 级额外等级。");
	}

	public DesperatePower() {
		super(2);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.DESPERATE_POWER;
	}

	public int lastChargeLevelBonus() {
		return level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", lastChargeLevelBonus());
	}
}
