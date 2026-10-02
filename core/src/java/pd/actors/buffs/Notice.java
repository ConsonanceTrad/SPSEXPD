/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Makes unintentional searches discover every searchable hidden tile in range. */
public class Notice extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Notice.class)
			.t("name", "敏锐")
			.t("desc", "敏锐的直觉使被动搜索必定发现范围内所有可搜索的隐藏事物。\n\n剩余效果时长：%s回合。");
	}


	public static final float DURATION = 30f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.FORESIGHT;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
