package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class ItemSteal extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ItemSteal.class)
			.t("name", "物品盗取")
			.t("desc", "你的下一次成功攻击会从目标身上盗取一件物品。\n\n剩余效果时长：%s回合。");
	}



	public static final float DURATION = 30f;
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.WEAPON; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
