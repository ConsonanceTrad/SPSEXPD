package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.ButterflypetEgg;
import pd.items.consum.eggs.FlyEgg;
import pd.items.consum.eggs.SnakeEgg;
import pd.messages.InlineText;
public class RandomEgg11 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg11.class)
			.t("name", "随机十一月灵魂")
			.t("desc", "获得一颗随机的十一月魂石，包括毒蛇、飞蝇、萤石粉蝶。");
	}


 public RandomEgg11() { super(SnakeEgg.class, FlyEgg.class, ButterflypetEgg.class); } }
