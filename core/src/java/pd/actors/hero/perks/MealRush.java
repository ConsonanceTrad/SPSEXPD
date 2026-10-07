/*
 * 速食一餐 —— 合并破碎的 IRON_STOMACH / ENERGIZING_MEAL / MYSTICAL_MEAL /
 * INVIGORATING_MEAL / FOCUSED_MEAL（进食 1 回合 + 资源充能）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class MealRush extends Perk {

	static {
		InlineText.of(MealRush.class)
				.t("title", "速食一餐")
				.t("desc", "1 级：进食只花费极短时间（约半个回合），并为本职业的资源（法杖/神器/武技/圣典）补充充能。\n2 级：进食不再消耗回合。");
	}

	public MealRush() {
		super(2);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.MEAL_RUSH;
	}

	/** 进食耗时倍率（0 = 不耗回合） */
	public float eatTimeMultiplier() {
		return level() >= 2 ? 0f : 0.5f;
	}

	/** 进食过程中的伤害抗性 */
	public float damageResistance() {
		return 0.5f + 0.25f * level();
	}

	/** 进食为本职业资源提供的充能（回合） */
	public float resourceCharge() {
		return 3f + 2f * level();
	}
}
