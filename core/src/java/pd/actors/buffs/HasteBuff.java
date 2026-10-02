/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** SPS-PD's two-times movement-speed effect. */
public class HasteBuff extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HasteBuff.class)
			.t("name", "极速")
			.t("desc", "你的移动速度提升为两倍。\n\n剩余效果时长：%s回合。");
	}




	public static final float DURATION = 10f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.HASTE;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
