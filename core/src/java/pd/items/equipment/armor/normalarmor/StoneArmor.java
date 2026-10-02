package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
public class StoneArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneArmor.class)
			.t("name", "石甲")
			.t("desc", "利用大地的魔法制作而成的护甲，坚硬而沉重。\n重型护甲");
	}


 public StoneArmor(){ super(3,.6f,0f,4,6,26,1,2,4,EquipmentEquipArmorBasicArmorDict.STONE_ARMOR); } }
