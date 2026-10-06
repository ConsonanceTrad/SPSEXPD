package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.PigpetEgg;
import pd.items.consum.eggs.StarKidEgg;
import pd.items.consum.eggs.StoneEgg;
import pd.messages.InlineText;
public class RandomEgg10 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg10.class)
			.t("name", "随机十月灵魂")
			.t("desc", "获得一颗随机的十月魂石，包括星芒、石拳石、像素猪。");
	}


 public RandomEgg10() { super(StarKidEgg.class, StoneEgg.class, PigpetEgg.class); } }
