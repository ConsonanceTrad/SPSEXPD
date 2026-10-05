/*
 * 连环打击 —— 合并破碎的 LINGERING_MAGIC / EMPOWERED_STRIKE / FOLLOWUP_STRIKE /
 * PATIENT_STRIKE / SUCKER_PUNCH / DEADLY_FOLLOWUP（各种「行动后强化下一次攻击」）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class ChainedStrike extends Perk {

	static {
		InlineText.of(ChainedStrike.class)
				.t("title", "连环打击")
				.t("desc", "施法后、投掷命中后、等待后、伏击时或使用武技后，你的下一次近战攻击获得额外伤害与精准。");
	}

	public ChainedStrike() {
		super(3);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.CHAINED_STRIKE;
	}

	/** 触发时的额外伤害 */
	public int bonusDamage() {
		return 1 + level();
	}

	/** 触发时的伤害倍率 */
	public float multiplier() {
		return 1f + 0.1f * level();
	}

	/** 触发条件的说明（供 UI/调试） */
	public String triggers() {
		return "施法后 / 投掷命中后 / 等待后 / 伏击时 / 使用武技后";
	}
}
