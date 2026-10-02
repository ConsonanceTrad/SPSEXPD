package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class RubberArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RubberArmor.class)
			.t("name", "橡胶衣")
			.t("desc", "一种可以紧贴全身的服装，一般用于角色扮演。\n轻型护甲");
	}
 public RubberArmor(){ super(2,3.7f,11f,2,0,8,-1,0,1,SpecificPlaceHolderDict.SOMETHING_0); } }
