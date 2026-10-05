/*
 * 应激护盾 —— 合并破碎的 BACKUP_BARRIER / PROTECTIVE_SHADOWS / AGGRESSIVE_BARRIER /
 * SHIELDING_DEW / EXCESS_CHARGE（在各种条件下获得护盾）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class SteadyShield extends Perk {

	static {
		InlineText.of(SteadyShield.class)
				.t("title", "应激护盾")
				.t("desc", "施法耗尽充能、隐身潜伏、低血使用武技、满血拾取露珠或魔杖满充能施法时，获得护盾。");
	}

	public SteadyShield() {
		super(3);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.STEADY_SHIELD;
	}

	/** 单次触发获得的护盾 */
	public int shieldOnCondition() {
		return 2 + level();
	}

	/** 触发条件说明 */
	public String triggers() {
		return "施法耗尽充能 / 隐身时累积 / 低血使用武技 / 满血拾取露珠 / 魔杖满充能施法";
	}
}
