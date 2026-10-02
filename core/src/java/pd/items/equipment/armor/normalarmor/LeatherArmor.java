package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
public class LeatherArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LeatherArmor.class)
			.t("name", "皮甲")
			.t("desc", "用鞣制的兽皮制成的护甲。没有布甲轻，但提供更好的防御。\n常规护甲");
	}


 public LeatherArmor(){ super(2,1.8f,5f,3,0,12,0,1,3,EquipmentEquipArmorBasicArmorDict.ARMOR_LEATHER_0); } }
