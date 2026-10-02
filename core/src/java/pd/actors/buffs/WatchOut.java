/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class WatchOut extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WatchOut.class)
			.t("name", "警戒")
			.t("desc", "这个生物周围的时间变得不稳定。剩余回合：%s。");
	}



	public static final float DURATION = 30f;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.LIGHT; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
