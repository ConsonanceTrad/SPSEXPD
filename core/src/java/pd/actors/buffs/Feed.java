/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** While active, each hostile mob kill permanently grants one maximum HP. */
public class Feed extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Feed.class)
			.t("name", "生命摄取")
			.t("desc", "每杀死一个敌对单位，永久获得1点生命上限。\n\n剩余效果时长：%s回合。");
	}

	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.WELL_FED; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
