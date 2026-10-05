/*
 * 好胃口 —— 进食更满足，并获得额外回复。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class GoodAppetite extends Perk {

	static {
		InlineText.of(GoodAppetite.class)
				.t("title", "好胃口")
				.t("desc", "进食更加满足，并且能获得 %d%% 的额外食物能量。");
	}

	public GoodAppetite() {
		super(1);
	}

	@Override
	public int image() {
		return PerkImageSheet.APPETITE_GOOD;
	}

	//裁决：释放到公共池（去掉职业限制）
	public float bonusMultiplier() {
		return 0.2f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(bonusMultiplier() * 100));
	}
}
