package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class VestArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(VestArmor.class)
			.t("name", "背心")
			.t("desc", "一件工厂生产的普通商品，虽然没法抵御伤害，但是十分适合运动。\n轻型护甲");
	}


 public VestArmor(){ super(1,4f,12f,1,0,2,-1,0,1,SpecificPlaceHolderDict.SOMETHING_0); } }
