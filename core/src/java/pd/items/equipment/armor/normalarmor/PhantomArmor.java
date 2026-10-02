package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.messages.InlineText;
public class PhantomArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PhantomArmor.class)
			.t("name", "幻影护甲")
			.t("desc", "先进高科技，外衣上装载着迷你投影仪，可以有效迷惑有眼睛的敌人。\n轻型护甲");
	}
 public PhantomArmor(){ super(6,2.4f,7f,2,0,35,-1,0,3,EquipmentEquipArmorBasicArmorDict.PHANTOM_ARMOR); } }
