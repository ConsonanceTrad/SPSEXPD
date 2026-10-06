package pd.items.consum.eggs.randomone;
import pd.items.consum.eggs.ButterflypetEgg;
import pd.items.consum.eggs.LitDemonEgg;
import pd.items.consum.eggs.SpiderpetEgg;
import pd.messages.InlineText;
public class RandomEgg6 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg6.class)
			.t("name", "随机六月灵魂")
			.t("desc", "获得一颗随机的六月魂石，包括链锯魔、植蛛、萤石粉蝶。");
	}


 public RandomEgg6() { super(LitDemonEgg.class, SpiderpetEgg.class, ButterflypetEgg.class); } }
