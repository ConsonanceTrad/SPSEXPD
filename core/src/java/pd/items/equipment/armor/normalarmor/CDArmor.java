package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
public class CDArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CDArmor.class)
			.t("name", "光碟甲")
			.t("desc", "一堆废弃光盘串成的护甲。很明显这是某个环保比赛的优秀作品。\n轻型护甲");
	}


 public CDArmor(){ super(3,3.4f,10f,3,0,15,-1,0,2,EquipmentEquipArmorBasicArmorDict.CD_ARMOR); } }
