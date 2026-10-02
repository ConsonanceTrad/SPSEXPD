package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.ButterflyPet;
import pd.actors.mobs.pets.LitDemon;
import pd.actors.mobs.pets.Spider;
import pd.messages.InlineText;
public class RandomEgg6 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg6.class)
			.t("name", "随机六月灵魂")
			.t("desc", "召唤一个随机的六月宠物，包括链锯魔、植蛛、萤石粉蝶。");
	}


 public RandomEgg6() { super(LitDemon.class, Spider.class, ButterflyPet.class); } }
