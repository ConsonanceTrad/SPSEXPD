/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.faithbuff;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class BalanceFaith extends FaithBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BalanceFaith.class)
			.t("name", "信仰-平衡派系")
			.t("desc", "来自首领和小首领的伤害降低25%%，对其造成的伤害提高50%%。");
	}



	@Override public int icon() { return BuffIndicator.ARMOR; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
