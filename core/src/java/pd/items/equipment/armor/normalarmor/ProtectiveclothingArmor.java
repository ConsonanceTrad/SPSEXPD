package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
public class ProtectiveclothingArmor extends NormalArmor {
	{
		image = EquipmentEquipArmorBasicArmorDict.HEAVY_SCALE_ARMOR;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ProtectiveclothingArmor.class)
			.t("name", "防护服")
			.t("desc", "部分有毒有害工厂工人必须穿着的工装，可以有效将有毒有害的环境与自身隔离。\n轻型护甲");
	}


 public ProtectiveclothingArmor(){ super(5,2.8f,8f,3,0,30,-1,0,3,SpecificPlaceHolderDict.SOMETHING_0); } }
