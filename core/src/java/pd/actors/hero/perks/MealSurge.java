/*
 * 饱食强化 —— 合并破碎的 HEARTY_MEAL / EMPOWERING_MEAL /
 * STRENGTHENING_MEAL（进食后强化下一次行动）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class MealSurge extends Perk {

	static {
		InlineText.of(MealSurge.class)
				.t("title", "饱食强化")
				.t("desc", "进食后：低血时立即回复生命；接下来的一次近战攻击与一次施法获得强化。");
	}

	public MealSurge() {
		super(2);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.MEAL_SURGE;
	}

	/** 低血进食的回复比例 */
	public float lowHealthHealRatio() {
		return 0.05f + 0.05f * level();
	}

	/** 进食后下一击的额外伤害 */
	public int nextAttackBonus() {
		return 2 + 2 * level();
	}

	/** 进食后下一次施法获得的护盾 */
	public int shieldOnNextCast() {
		return 3 + 2 * level();
	}
}
