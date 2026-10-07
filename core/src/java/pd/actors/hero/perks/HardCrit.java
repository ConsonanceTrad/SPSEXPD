/*
 * 重击 —— 提高暴击伤害倍率。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class HardCrit extends Perk {

	static {
		InlineText.of(HardCrit.class)
				.t("title", "重击")
				.t("desc", "提高 %s%% 的暴击伤害倍率。");
	}

	public HardCrit() {
		super(5);
		addTags(Tag.Crit);
	}

	@Override
	public int image() {
		return PerkImageSheet.CRIT_HARD;
	}

	public float critDamageBonus() {
		return level() * 0.25f + 0.05f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(critDamageBonus() * 100)));
	}
}
