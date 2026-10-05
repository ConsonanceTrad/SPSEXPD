/*
 * 法杖回收 —— 来自破碎 WAND_PRESERVATION。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class WandPreservation extends Perk {

	static {
		InlineText.of(WandPreservation.class)
				.t("title", "法杖回收")
				.t("desc", "将新的法杖注入魔杖时，旧法杖会被回收为 0 级法杖（共可执行 %d 次）。");
	}

	public WandPreservation() {
		super(2);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.WAND_PRESERVATION;
	}

	public int uses() {
		return level();
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", uses());
	}
}
