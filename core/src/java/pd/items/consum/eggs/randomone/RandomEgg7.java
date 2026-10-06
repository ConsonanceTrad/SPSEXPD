package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.DwarfBoyEgg;
import pd.items.consum.eggs.FrogpetEgg;
import pd.items.consum.eggs.GentleCrabEgg;
import pd.messages.InlineText;
public class RandomEgg7 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg7.class)
			.t("name", "随机七月灵魂")
			.t("desc", "获得一颗随机的七月魂石，包括绅士蟹、矮人学徒、呆头蛙。");
	}


 public RandomEgg7() { super(GentleCrabEgg.class, DwarfBoyEgg.class, FrogpetEgg.class); } }
