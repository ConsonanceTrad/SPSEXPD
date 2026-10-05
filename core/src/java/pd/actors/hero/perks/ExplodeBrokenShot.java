/*
 * 爆炸射击 —— 投掷武器折断时产生爆炸。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class ExplodeBrokenShot extends Perk {

	static {
		InlineText.of(ExplodeBrokenShot.class)
				.t("title", "爆炸射击")
				.t("desc", "投掷武器在折断时会爆炸，对周围敌人造成大量伤害。");
	}

	public ExplodeBrokenShot() {
		super(1);
		addTags(Tag.Ranged);
	}

	@Override
	public int image() {
		return PerkImageSheet.SHOT_EXPLODE;
	}

	/** 爆炸伤害 = 武器伤害 × 该倍率 */
	public float explosionMultiplier() {
		return 1.5f;
	}
}
