/*
 * 濒死回复 —— 低生命值受击时触发额外回复。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class LowHealthRegeneration extends Perk {

	static {
		InlineText.of(LowHealthRegeneration.class)
				.t("title", "濒死回复")
				.t("desc", "受到伤害后，若生命值较低则获得一次额外回复。等级越高，触发阈值与回复量越高。");
	}

	public LowHealthRegeneration() {
		super(5);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.LOW_HEALTH_REG;
	}

	/** 触发阈值（生命百分比） */
	public float threshold() {
		return 0.3f + 0.05f * level();
	}

	/** 回复量（最大生命的百分比） */
	public float healRatio() {
		return 0.05f + 0.02f * level();
	}
}
