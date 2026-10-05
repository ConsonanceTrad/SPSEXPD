/*
 * 乘胜追击 —— 合并破碎的 LETHAL_MOMENTUM / CLEAVE / LETHAL_DEFENSE / LETHAL_HASTE
 * （击杀之后的连锁收益）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class MomentumSlayer extends Perk {

	static {
		InlineText.of(MomentumSlayer.class)
				.t("title", "乘胜追击")
				.t("desc", "击杀敌人后：有几率不消耗回合、连击数得以继承、纹章护盾冷却缩短，并获得短暂加速。");
	}

	public MomentumSlayer() {
		super(2);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.MOMENTUM_SLAYER;
	}

	/** 击杀不消耗回合的几率 */
	public float freeKillChance() {
		return 0.67f;
	}

	/** 连击数继承的回合数 */
	public int comboRetention() {
		return 30 + 15 * level();
	}

	/** 纹章护盾冷却缩减回合 */
	public int cooldownReduction() {
		return 50;
	}

	/** 击杀后的移动耗时倍率 */
	public float hasteMultiplier() {
		return 0.8f;
	}
}
