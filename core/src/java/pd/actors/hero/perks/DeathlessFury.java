/*
 * 不朽骤雨 —— 来自破碎 DEATHLESS_FURY。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class DeathlessFury extends Perk {

	static {
		InlineText.of(DeathlessFury.class)
				.t("title", "不朽骤雨")
				.t("desc", "即将死亡且怒气充盈时会自动狂暴化；触发后需 %s 个英雄等级冷却。");
	}

	public DeathlessFury() {
		super(3);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.DEATHLESS_FURY;
	}

	/** 触发的怒气阈值 */
	public float rageThreshold() {
		return Math.max(0.5f, 1f - 0.15f * level());
	}

	/** 冷却所需英雄等级 */
	public int cooldownLevels() {
		return Math.max(1, 4 - level());
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(cooldownLevels()));
	}
}
