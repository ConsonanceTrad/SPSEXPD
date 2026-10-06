package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.DaturaEgg;
import pd.items.consum.eggs.DogpetEgg;
import pd.items.consum.eggs.KodoraEgg;
import pd.messages.InlineText;
public class RandomEgg1 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg1.class)
			.t("name", "随机一月灵魂")
			.t("desc", "获得一颗随机的一月魂石，包括柯多拉、忠犬、曼陀罗。");
	}


 public RandomEgg1() { super(KodoraEgg.class, DogpetEgg.class, DaturaEgg.class); } }
