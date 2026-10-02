/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class LingBless extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LingBless.class)
			.t("name", "澪祷之愿")
			.t("desc", "澪给你祝福，少量的提升了你的当前闪避和速度。\n\n持续时间：%s。");
	}


	public static final float DURATION = 200f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.BLESS;
	}
}
