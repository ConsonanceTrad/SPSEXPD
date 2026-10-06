package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.FoxHelperEgg;
import pd.items.consum.eggs.GentleCrabEgg;
import pd.items.consum.eggs.StoneEgg;
import pd.messages.InlineText;
public class RandomEgg2 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg2.class)
			.t("name", "随机二月灵魂")
			.t("desc", "获得一颗随机的二月魂石，包括绅士蟹、石拳石、狐女仆。");
	}


 public RandomEgg2() { super(GentleCrabEgg.class, StoneEgg.class, FoxHelperEgg.class); } }
