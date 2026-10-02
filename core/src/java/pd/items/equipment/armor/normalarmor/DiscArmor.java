package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class DiscArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DiscArmor.class)
			.t("name", "碟甲")
			.t("desc", "这种盔甲只是将金属片缝在布料上。它虽然耐用，但有着很大的体积。\n常规护甲");
	}


 public DiscArmor(){ super(3,1.6f,4f,4,0,20,0,1,3,SpecificPlaceHolderDict.SOMETHING_0); } }
