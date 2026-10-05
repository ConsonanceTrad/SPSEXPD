/*
 * 节食 —— 更耐饥饿（饥饿累积速度降低）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Dieting extends Perk {

	static {
		InlineText.of(Dieting.class)
				.t("title", "节食")
				.t("desc", "你更加耐饿，饥饿累积速度降低 %d%%。");
	}

	public Dieting() {
		super(3);   // 裁决：更改为可提升特质
	}

	@Override
	public int image() {
		return PerkImageSheet.DIETING;
	}

	/** 饥饿累积倍率（越小越耐饿） */
	public float hungerMultiplier() {
		return Math.max(0.4f, 1f - 0.2f * level());
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round((1f - hungerMultiplier()) * 100));
	}
}
