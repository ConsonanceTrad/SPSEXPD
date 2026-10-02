package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
public class StyrofoamArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StyrofoamArmor.class)
			.t("name", "塑料泡沫甲")
			.t("desc", "用塑料泡沫充当护甲，这能小幅减轻外来的冲击。\n轻型护甲");
	}


 public StyrofoamArmor(){ super(4,3f,9f,4,0,22,-1,0,2,EquipmentEquipArmorBasicArmorDict.STYROFOAM_ARMOR); } }
