package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
public class CeramicsArmor extends NormalArmor {
	{
		image = EquipmentEquipArmorBasicArmorDict.CERAMICS_ARMOR;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CeramicsArmor.class)
			.t("name", "陶甲")
			.t("desc", "粘土火中烧，结实又可靠。\n重型护甲");
	}


 public CeramicsArmor(){ super(2,.8f,.5f,3,4,18,1,2,3,EquipmentEquipArmorBasicArmorDict.CERAMICS_ARMOR); } }
