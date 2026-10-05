/*
 * 终结射击 —— 投掷击杀后，接下来的若干动作耗时减少。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class FinishingShot extends Perk {

	static {
		InlineText.of(FinishingShot.class)
				.t("title", "终结射击")
				.t("desc", "以投掷武器击杀敌人后，你接下来的数个动作将更快完成。");
	}

	public FinishingShot() {
		super(1);
		addTags(Tag.Ranged);
	}

	@Override
	public int image() {
		return PerkImageSheet.FINISHING_SHOT;
	}

	public float speedMultiplier() {
		return 0.8f;
	}
}
