/*
 * 暴食 —— 更快陷入饥饿（负向特质）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class RavenousAppetite extends Perk {

	static {
		InlineText.of(RavenousAppetite.class)
				.t("title", "暴食")
				.t("desc", "你饿得更快，饥饿累积速度提高 %d%%。");
	}

	public RavenousAppetite() {
		super(1);
	}

	@Override
	public int image() {
		return PerkImageSheet.APPETITE_RAVENOUS;
	}

	/** 饥饿累积倍率（越大越快饿） */
	public float hungerMultiplier() {
		return 1.5f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round((hungerMultiplier() - 1f) * 100));
	}
}
