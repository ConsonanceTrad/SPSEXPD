package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
public class WoodenArmor extends NormalArmor {
	{
		image = EquipmentEquipArmorBasicArmorDict.WOODEN_ARMOR;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WoodenArmor.class)
			.t("name", "木甲")
			.t("desc", "精选上等白桦树皮所制，坚固耐用，但极不舒适。\n重型护甲");
	}


 public WoodenArmor(){ super(1,1f,1f,2,2,6,1,2,3,EquipmentEquipArmorBasicArmorDict.WOODEN_ARMOR); } }
