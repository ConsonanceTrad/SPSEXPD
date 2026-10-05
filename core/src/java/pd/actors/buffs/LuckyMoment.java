/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/**
 * SPSEXPD: 彩虹三色堇——临时提升幸运。
 */
public class LuckyMoment extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LuckyMoment.class)
			.t("name", "幸运时刻")
			.t("desc", "五彩的祝福围绕着你，你的幸运暂时提升了。\n\n剩余回合：%s");
	}

	public static final int BONUS = 2;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.BLESS;
	}
}
