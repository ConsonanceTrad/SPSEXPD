/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.actbuff;

import pd.actors.buffs.FlavourBuff;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Nmstop extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Nmstop.class)
			.t("name", "纳米熔断")
			.t("desc", "纳米机器人停止增殖并逐步分解。\n\n剩余时长：%s回合");
	}



	public static final float DURATION = 10f;
	{ type = buffType.NEUTRAL; }
	@Override public int icon() { return BuffIndicator.FROST; }
}
