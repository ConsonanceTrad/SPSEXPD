package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentBagsDict;
public class MailArmor extends NormalArmor {
	{
		image = EquipmentBagsDict.HEART_OF_SCARECROW_0;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MailArmor.class)
			.t("name", "链甲")
			.t("desc", "由金属链环环相扣制成的一套结实又不失灵活的护甲。\n常规护甲");
	}


 public MailArmor(){ super(4,1.4f,3f,4,0,28,0,1,3,EquipmentEquipArmorBasicArmorDict.ARMOR_MAIL_0); } }
