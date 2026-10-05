/*
 * 纯粹暴击 —— 裁决：改为「暴击伤害的 50% 变为纯粹伤害」（无视护甲）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class PureCrit extends Perk {

	static {
		InlineText.of(PureCrit.class)
				.t("title", "纯粹暴击")
				.t("desc", "暴击时，暴击伤害的 %d%% 变为纯粹伤害（无视护甲）。");
	}

	public PureCrit() {
		super(1);
		addTags(Tag.Crit);
	}

	@Override
	public int image() {
		return PerkImageSheet.CRIT_PURE;
	}

	/** 暴击伤害中转为纯粹伤害的比例 */
	public float pureRatio() {
		return 0.5f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(pureRatio() * 100));
	}
}
