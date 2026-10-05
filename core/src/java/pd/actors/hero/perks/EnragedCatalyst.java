/*
 * 怒气导魔 —— 来自破碎 ENRAGED_CATALYST。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class EnragedCatalyst extends Perk {

	static {
		InlineText.of(EnragedCatalyst.class)
				.t("title", "怒气导魔")
				.t("desc", "怒气越充盈，武器上的附魔与诅咒触发概率越高（满怒气时提升至 %d%%）。");
	}

	public EnragedCatalyst() {
		super(3);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.ENRAGED_CATALYST;
	}

	/** 满怒气时的触发概率倍率 */
	public float enchantProcMultiplier() {
		return 1f + 0.15f * level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(enchantProcMultiplier() * 100));
	}
}
