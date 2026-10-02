/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Doubles movement speed, raises outgoing damage by 50%, and incoming damage by 10%. */
public class SpeedImbue extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpeedImbue.class)
			.t("name", "速度灌注")
			.t("desc", "速度提高到两倍，攻击伤害提高50%%，但受到的伤害提高10%%。\n\n剩余效果时长：%s回合。");
	}



	{ type = buffType.NEUTRAL; announced = true; }
	@Override public int icon() { return BuffIndicator.HASTE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
