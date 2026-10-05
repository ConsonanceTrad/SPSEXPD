/*
 * 致命准备 —— 合并破碎的 ENHANCED_LETHALITY / ASSASSINS_REACH /
 * BOUNTY_HUNTER / COMBINED_LETHALITY（刺杀准备阶段的各项强化）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class LethalPrep extends Perk {

	static {
		InlineText.of(LethalPrep.class)
				.t("title", "致命准备")
				.t("desc", "提升斩杀生命阈值与闪现距离，被技能斩杀的战利品掉落率提高，武技连击可处决低血敌人。");
	}

	public LethalPrep() {
		super(2);
		addTags(Tag.Melee, Tag.Ranged);
	}

	@Override
	public int image() {
		return PerkImageSheet.LETHAL_PREP;
	}

	/** 斩杀阈值提升比例 */
	public float executeThresholdBonus() {
		return 0.33f + 0.12f * level();
	}

	/** 闪现距离加成（格） */
	public int blinkRangeBonus() {
		return 2 * level();
	}

	/** 战利品掉落率加成 */
	public float lootChanceBonus() {
		return 0.02f * level();
	}

	/** 处决阈值（非 Boss） */
	public float executeHpThreshold() {
		return 0.13f + 0.07f * level();
	}
}
