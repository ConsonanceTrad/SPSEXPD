package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class DeadRaise extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DeadRaise.class)
			.t("name", "怨念环绕")
			.t("desc", "你感觉身边充满了强烈的怨念，仿佛有什么东西即将出现。\n\n剩余效果时长：%s回合。");
	}



	public static final float DURATION = 5f;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.TERROR; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
