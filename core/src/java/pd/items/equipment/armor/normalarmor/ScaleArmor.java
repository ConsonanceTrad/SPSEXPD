package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
public class ScaleArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScaleArmor.class)
			.t("name", "鳞甲")
			.t("desc", "在厚实的皮背心中缝入金属鳞片，形成了灵活而高防御的护甲。\n常规护甲");
	}
 public ScaleArmor(){ super(5,1f,2f,3,0,36,0,1,3,EquipmentEquipArmorBasicArmorDict.ARMOR_SCALE_0); } }
