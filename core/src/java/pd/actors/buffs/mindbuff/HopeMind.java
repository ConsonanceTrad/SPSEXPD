/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.mindbuff;

import pd.actors.buffs.Buff;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Persistent positive mental state: each future level grants one extra maximum HP. */
public class HopeMind extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HopeMind.class)
			.t("name", "疯狂-希望")
			.t("desc", "以后每次升级都会额外增加1点生命上限。");
	}



	{ type = buffType.POSITIVE; announced = true; }
	@Override public boolean act() { if (target != null && target.isAlive()) spend(TICK); return true; }
	@Override public int icon() { return BuffIndicator.MIND_VISION; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
