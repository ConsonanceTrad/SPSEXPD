/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.messages.InlineText;

public class Bone extends SpsBossKey {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Bone.class)
			.t("name", "亡灵短骨")
			.t("desc", "我十分怀疑这一切是亡灵的阴谋。无论如何，去看看吧。")
			.t("ac_port", "使用");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected int destination() { return 11; }
	@Override protected boolean bossKilled() { return Dungeon.skeletonKingKilled; }
}
