/*
 * 夜视 —— 提高视野距离。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.messages.InlineText;

public class NightVision extends Perk {

	static {
		InlineText.of(NightVision.class)
				.t("title", "夜视")
				.t("desc", "提高视野距离，在黑暗中看得更远。");
	}

	public NightVision() {
		super(2);
	}

	@Override
	public int image() {
		return PerkImageSheet.NIGHT_VISION;
	}

	//裁决：释放到公共池（去掉子职业限制，可直接升满 2 级）
	/** 额外视野距离 */
	public int extraView() {
		return level();
	}
}
