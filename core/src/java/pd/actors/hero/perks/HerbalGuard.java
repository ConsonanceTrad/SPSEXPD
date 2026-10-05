/*
 * 草木庇护 —— 合并破碎的 NATURES_AID / BARKSKIN（植物相关获得树肤护甲）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class HerbalGuard extends Perk {

	static {
		InlineText.of(HerbalGuard.class)
				.t("title", "草木庇护")
				.t("desc", "视野内的植物效果触发，或踏上未枯萎的植物时，获得逐回合衰减的树肤护甲。");
	}

	public HerbalGuard() {
		super(3);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.HERBAL_GUARD;
	}

	/** 触发时获得的树肤点数 */
	public int barkskinOnPlant() {
		return 1 + level();
	}

	/** 树肤的衰减周期（回合/点） */
	public int decayTurns() {
		return 3;
	}
}
