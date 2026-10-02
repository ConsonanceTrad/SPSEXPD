package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Cold extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Cold.class)
			.t("name", "寒冷")
			.t("desc", "寒冷会减缓你的移动。\n\n剩余效果时长：%s回合。");
	}



	public static final float DURATION = 10f;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.FROST; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
