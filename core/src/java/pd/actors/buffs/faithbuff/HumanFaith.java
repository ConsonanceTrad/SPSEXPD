/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.faithbuff;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class HumanFaith extends FaithBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HumanFaith.class)
			.t("name", "信仰-神圣派系")
			.t("desc", "来自神圣派系的伤害降低25%%，对恶魔派系造成的伤害提高50%%。");
	}

	@Override public int icon() { return BuffIndicator.BLESS; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
