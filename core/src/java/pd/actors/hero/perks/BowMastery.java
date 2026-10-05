/*
 * 弓术联动 —— 合并破碎的 SHARED_ENCHANTMENT / SHARED_UPGRADES /
 * DURABLE_PROJECTILES / POINT_BLANK（投掷武器与灵能弓的联动）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class BowMastery extends Perk {

	static {
		InlineText.of(BowMastery.class)
				.t("title", "弓术联动")
				.t("desc", "投掷武器有几率附带灵能弓的附魔与升级，耐久更高；近战距离投掷的精准惩罚减轻。");
	}

	public BowMastery() {
		super(3);
		addTags(Tag.Ranged);
	}

	@Override
	public int image() {
		return PerkImageSheet.BOW_MASTERY;
	}

	/** 附带附魔的几率 */
	public float enchantCarryChance() {
		return 0.33f * level();
	}

	/** 附带升级的伤害比例 */
	public float upgradeCarryRatio() {
		return 0.16f * level();
	}

	/** 投掷武器耐久加成 */
	public float durabilityBonus() {
		return 0.5f * level();
	}

	/** 抵近投掷的精准修正（减轻惩罚） */
	public float closeRangeAccuracy() {
		return 0.25f;
	}
}
