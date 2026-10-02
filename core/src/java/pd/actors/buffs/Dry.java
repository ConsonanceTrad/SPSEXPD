/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Dry extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Dry.class)
			.t("name", "干燥")
			.t("desc", "你的身体严重缺水。剩余回合：%s。");
	}

	public static final float DURATION = 10f;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.OOZE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
