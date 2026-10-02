/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Performer state: attacks sometimes gain 20% damage and hits sometimes lose 20% damage. */
public class HighVoice extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HighVoice.class)
			.t("name", "高音")
			.t("desc", "演奏有时会强化攻击，并削弱受到的伤害。\n\n剩余效果时长：%s回合。");
	}



	{ type = buffType.NEUTRAL; announced = true; }
	@Override public int icon() { return BuffIndicator.HEART; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
