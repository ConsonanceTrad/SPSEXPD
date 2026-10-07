/*
 * 精准打击 —— 来自破碎 PRECISE_ASSAULT。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class PreciseAssault extends Perk {

	static {
		InlineText.of(PreciseAssault.class)
				.t("title", "精准打击")
				.t("desc", "使用武技后，%d 回合内的下一次近战攻击具有 %s 倍精准。");
	}

	public PreciseAssault() {
		super(2);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.PRECISE_ASSAULT;
	}

	public float window() {
		return 5f;
	}

	public float accuracyMultiplier() {
		return 2f + (level() - 1);
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc",
				Math.round(window()), num(Math.round(accuracyMultiplier())));
	}
}
