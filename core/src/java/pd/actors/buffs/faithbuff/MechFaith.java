/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.faithbuff;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class MechFaith extends FaithBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MechFaith.class)
			.t("name", "信仰-机械派系")
			.t("desc", "来自机械派系的伤害降低25%%，对自然派系造成的伤害提高50%%。");
	}



	@Override public int icon() { return BuffIndicator.RECHARGING; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
