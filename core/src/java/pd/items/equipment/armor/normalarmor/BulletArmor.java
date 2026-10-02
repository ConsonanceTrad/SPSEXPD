package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
public class BulletArmor extends NormalArmor {
	{
		image = EquipmentEquipArmorBasicArmorDict.HEAVY_CHAIN_ARMOR;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BulletArmor.class)
			.t("name", "防弹衣")
			.t("desc", "基本上就是常规的防弹衣。\n重型护甲");
	}


 public BulletArmor(){ super(5,.2f,-1f,2,10,46,1,2,5,SpecificPlaceHolderDict.SOMETHING_0); } }
