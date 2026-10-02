/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.items.consum.medicine.Pill;
import pd.messages.Messages;
import pd.utils.GLog;
import pd.messages.InlineText;

public class SellMushroom extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SellMushroom.class)
			.t("name", "好看的蘑菇")
			.t("no", "味道一般，像是空气。")
			.t("desc", "地狱三头犬喜欢的蘑菇。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onUse(Hero hero) { GLog.w(Messages.get(this, "no")); }
	@Override public int value() { return 100 * quantity; }
}
