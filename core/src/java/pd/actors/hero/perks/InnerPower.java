/*
 * 内力修习 —— 合并破碎的 WEAPON_RECHARGING / VARIED_CHARGE / COMBINED_ENERGY /
 * MONASTIC_VIGOR / UNENCUMBERED_SPIRIT（武技/内力/充能的回复）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class InnerPower extends Perk {

	static {
		InlineText.of(InnerPower.class)
				.t("title", "内力修习")
				.t("desc", "充能状态下周期性恢复武技充能；使用不同武技或武功可回复内力；装备低阶装备时内力获取更快。");
	}

	public InnerPower() {
		super(3);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.INNER_POWER;
	}

	/** 每隔多少回合恢复 1 点武技充能 */
	public int chargeEveryTurns() {
		return Math.max(5, 15 - 5 * level());
	}

	/** 使用两种不同武技时的充能收益 */
	public float chargeOnStanceSwitch() {
		return 0.17f * level();
	}

	/** 使用武技+武功组合时的内力回复 */
	public int innerPowerRecover() {
		return level();
	}

	/** 低阶装备带来的内力获取加成 */
	public float lowTierEquipBonus() {
		return 0.5f * level();
	}
}
