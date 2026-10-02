/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.sellitem;

import pd.atlas.items.EquipmentJewelleryArtifactDict;
import pd.messages.InlineText;


public class LingHeart extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LingHeart.class)
			.t("name", "水晶项链")
			.t("desc", "_物理防御总量提升30%，治疗效果提升1.2倍，携带背包内生效，可抵挡一次死亡_，Ling如是说");
	}

	{
		image = EquipmentJewelleryArtifactDict.LING_HEART_0;
		stackable = true;
	}
	@Override public int value() { return 100000 * quantity; }
	@Override public String info() { return desc(); }
}
