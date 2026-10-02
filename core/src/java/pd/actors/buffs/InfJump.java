/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class InfJump extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(InfJump.class)
			.t("name", "无限跳跃")
			.t("desc", "跳跃不会消耗鞋子的充能。\n\n剩余效果时长：%s回合。");
	}

	public static final float DURATION = 30f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override public int icon() { return BuffIndicator.LEVITATION; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
