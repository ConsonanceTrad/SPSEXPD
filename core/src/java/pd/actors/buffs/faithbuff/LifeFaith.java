/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.faithbuff;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class LifeFaith extends FaithBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LifeFaith.class)
			.t("name", "信仰-自然派系")
			.t("desc", "来自自然派系的伤害降低25%%，对机械派系造成的伤害提高50%%。");
	}



	@Override public int icon() { return BuffIndicator.HERB_HEALING; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
