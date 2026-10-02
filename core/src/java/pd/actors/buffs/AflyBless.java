/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Alfred's original temporary blessing: +1 strength and +6% base loot chance. */
public class AflyBless extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(AflyBless.class)
			.t("name", "不思议智慧")
			.t("desc", "不思议的智慧带给你启发，少量提升你的当前力量和幸运。\n\n持续时间：%s回合。");
	}



	{
		type = buffType.POSITIVE;
		announced = true;
	}
	@Override public int icon() { return BuffIndicator.BLESS; }
}
