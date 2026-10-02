/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.faithbuff;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class DemonFaith extends FaithBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DemonFaith.class)
			.t("name", "信仰-恶魔派系")
			.t("desc", "来自恶魔派系的伤害降低25%%，对神圣派系造成的伤害提高50%%。");
	}

	@Override public int icon() { return BuffIndicator.CORRUPT; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
