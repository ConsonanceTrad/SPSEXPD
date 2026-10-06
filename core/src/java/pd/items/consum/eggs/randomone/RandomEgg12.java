package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.LitDemonEgg;
import pd.items.consum.eggs.MonkeyEgg;
import pd.items.consum.eggs.SpiderpetEgg;
import pd.messages.InlineText;
public class RandomEgg12 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg12.class)
			.t("name", "随机十二月灵魂")
			.t("desc", "获得一颗随机的十二月魂石，包括链锯魔、植蛛、绿皮猴。");
	}


 public RandomEgg12() { super(LitDemonEgg.class, SpiderpetEgg.class, MonkeyEgg.class); } }
