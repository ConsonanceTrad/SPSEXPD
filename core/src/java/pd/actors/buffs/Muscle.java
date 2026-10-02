/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Legacy SPS strength infusion. The actual +2 STR is applied by Hero.STR(). */
public class Muscle extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Muscle.class)
			.t("name", "肌肉强化")
			.t("desc", "力量提高2点。\n\n剩余效果时长：%s回合。");
	}



	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.FURY; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
