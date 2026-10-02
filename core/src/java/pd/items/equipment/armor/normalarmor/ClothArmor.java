package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
public class ClothArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ClothArmor.class)
			.t("name", "布甲")
			.t("desc", "这件轻便的护甲能提供最基本的防御。\n常规护甲");
	}
 public ClothArmor(){ super(1,2f,6f,3,0,4,0,1,3,EquipmentEquipArmorBasicArmorDict.ARMOR_CLOTH_0); } }
