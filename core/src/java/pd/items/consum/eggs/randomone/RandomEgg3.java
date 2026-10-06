package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.DwarfBoyEgg;
import pd.items.consum.eggs.FrogpetEgg;
import pd.items.consum.eggs.RibbonRatEgg;
import pd.messages.InlineText;
public class RandomEgg3 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg3.class)
			.t("name", "随机三月灵魂")
			.t("desc", "获得一颗随机的三月魂石，包括缎带鼠、矮人学徒、呆头蛙。");
	}


 public RandomEgg3() { super(RibbonRatEgg.class, DwarfBoyEgg.class, FrogpetEgg.class); } }
