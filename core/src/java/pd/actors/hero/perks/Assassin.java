/*
 * 刺客 —— 偷袭造成额外伤害。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Assassin extends Perk {

	static {
		InlineText.of(Assassin.class)
				.t("title", "刺客")
				.t("desc", "进行偷袭时对敌人造成额外伤害。");
	}

	public Assassin() {
		super(1);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.ASSASSIN;
	}

	/** 偷袭时的伤害倍率 */
	public float surpriseMultiplier() {
		return 1.5f;
	}
}
