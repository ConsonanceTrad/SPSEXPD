package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.ChocoboEgg;
import pd.items.consum.eggs.PigpetEgg;
import pd.items.consum.eggs.SnakeEgg;
import pd.messages.InlineText;
public class RandomEgg5 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg5.class)
			.t("name", "随机五月灵魂")
			.t("desc", "获得一颗随机的五月魂石，包括毒蛇、陆行鸟、像素猪。");
	}


 public RandomEgg5() { super(SnakeEgg.class, ChocoboEgg.class, PigpetEgg.class); } }
