/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class FunnyBuff extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FunnyBuff.class)
			.t("name", "滑稽")
			.t("desc", "一切都显得十分滑稽。\n\n剩余时间：%s回合。");
	}


	public static final float DURATION = 30f;

	{
		type = buffType.NEUTRAL;
	}

	@Override public int icon() { return BuffIndicator.BLESS; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
