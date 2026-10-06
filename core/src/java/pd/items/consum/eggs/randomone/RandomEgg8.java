package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.DogpetEgg;
import pd.items.consum.eggs.FoxHelperEgg;
import pd.items.consum.eggs.StarKidEgg;
import pd.messages.InlineText;
public class RandomEgg8 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg8.class)
			.t("name", "随机八月灵魂")
			.t("desc", "获得一颗随机的八月魂石，包括星芒、忠犬、狐女仆。");
	}


 public RandomEgg8() { super(StarKidEgg.class, DogpetEgg.class, FoxHelperEgg.class); } }
