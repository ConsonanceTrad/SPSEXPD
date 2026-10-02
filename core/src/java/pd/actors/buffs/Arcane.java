/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Marker used by SPS effects which temporarily amplify magic. */
public class Arcane extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Arcane.class)
			.t("name", "奥术强化")
			.t("desc", "魔法力量暂时得到强化。\n\n剩余回合：%s。");
	}



	public static final float DURATION = 30f;
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.IMMUNITY; }
}
