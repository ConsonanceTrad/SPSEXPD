package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class MultiplelayerArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MultiplelayerArmor.class)
			.t("name", "复层甲")
			.t("desc", "这种护甲有内外两层，以敏捷为代价换取防御。\n重型护甲");
	}
 public MultiplelayerArmor(){ super(4,.4f,-.5f,3,8,36,1,2,4,SpecificPlaceHolderDict.SOMETHING_0); } }
