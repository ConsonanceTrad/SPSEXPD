/*
 * 远程射击 —— 投掷伤害随距离提升。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class RangedShot extends Perk {

	static {
		InlineText.of(RangedShot.class)
				.t("title", "远程射击")
				.t("desc", "投掷武器造成的伤害随投掷距离提高。");
	}

	public RangedShot() {
		super(1);
		addTags(Tag.Ranged);
	}

	@Override
	public int image() {
		return PerkImageSheet.RANGED_SHOT;
	}

	/** 每格距离增加的伤害比例 */
	public float perCell() {
		return 0.05f;
	}

	public float multiplier(int distance) {
		return 1f + perCell() * Math.max(0, distance - 1);
	}
}
