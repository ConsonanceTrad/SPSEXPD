/*
 * 战技强化 —— 来自破碎 ENHANCED_COMBO。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class EnhancedCombo extends Perk {

	static {
		InlineText.of(EnhancedCombo.class)
				.t("title", "战技强化")
				.t("desc", "连击数达到 %d 或以上时，冲击的击退距离提升并附带眩晕，可将敌人击落深渊。");
	}

	public EnhancedCombo() {
		super(2);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.ENHANCED_COMBO;
	}

	public int comboThreshold() {
		return Math.max(4, 7 - (level() - 1));
	}

	public int knockback() {
		return 3;
	}

	public float stunTurns() {
		return 1f + level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", comboThreshold());
	}
}
