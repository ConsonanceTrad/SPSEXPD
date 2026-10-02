package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class MachineArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MachineArmor.class)
			.t("name", "机械甲")
			.t("desc", "常规的板甲配备核心电源，外设四只机械臂，就是会发出巨大噪音。\n重型护甲");
	}
 public MachineArmor(){ super(6,0f,-2f,1,15,60,1,3,5,SpecificPlaceHolderDict.SOMETHING_0); } }
