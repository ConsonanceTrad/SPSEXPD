package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
public class PlateArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PlateArmor.class)
			.t("name", "板甲")
			.t("desc", "厚重的金属板拼接到一起，为能承受其骇人重量的冒险者提供无与伦比的防御。\n常规护甲");
	}
 public PlateArmor(){ super(6,1.2f,1f,3,0,44,0,1,3,EquipmentEquipArmorBasicArmorDict.ARMOR_PLATE_0); } }
