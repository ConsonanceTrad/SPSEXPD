/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.messages.InlineText;

public class ConchShell extends SpsBossKey {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ConchShell.class)
			.t("name", "巨蟹海螺")
			.t("desc", "为什么远古洞穴里会有这种东西？你的直觉告诉你有个大家伙在里面。")
			.t("ac_port", "使用");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected int destination() { return 12; }
	@Override protected boolean bossKilled() { return Dungeon.crabKingKilled; }
}
