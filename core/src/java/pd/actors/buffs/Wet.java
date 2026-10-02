/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Wet extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Wet.class)
			.t("name", "潮湿")
			.t("desc", "水浸湿了身体，会令某些元素效果更加危险。剩余回合：%s。");
	}



	public static final float DURATION = 10f;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.FROST; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
