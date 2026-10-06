package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.FlyEgg;
import pd.items.consum.eggs.KodoraEgg;
import pd.items.consum.eggs.MonkeyEgg;
import pd.messages.InlineText;
public class RandomEgg4 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg4.class)
			.t("name", "随机四月灵魂")
			.t("desc", "获得一颗随机的四月魂石，包括柯多拉、飞蝇、绿皮猴。");
	}


 public RandomEgg4() { super(KodoraEgg.class, FlyEgg.class, MonkeyEgg.class); } }
