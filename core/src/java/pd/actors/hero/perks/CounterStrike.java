/*
 * 反击 —— 闪避敌人近战后，下一次攻击获得强化。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class CounterStrike extends Perk {

	static {
		InlineText.of(CounterStrike.class)
				.t("title", "反击")
				.t("desc", "每次成功闪避敌人的近战攻击，都会强化你接下来对该目标的攻击。");
	}

	public CounterStrike() {
		super(1);
		addTags(Tag.Evade, Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.COUNTER_STRIKE;
	}

	public float damageMultiplier() {
		return 1.5f;
	}
}
