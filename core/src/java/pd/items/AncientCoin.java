/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.messages.InlineText;

public class AncientCoin extends SpsBossKey {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AncientCoin.class)
			.t("name", "上朝贡物")
			.t("desc", "这东西可以作为信物，让盗贼领主召唤你前去。")
			.t("ac_port", "使用");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected int destination() { return 13; }
	@Override protected boolean bossKilled() { return Dungeon.banditKingKilled; }
}
