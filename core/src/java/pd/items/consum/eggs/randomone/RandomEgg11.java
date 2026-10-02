package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.ButterflyPet;
import pd.actors.mobs.pets.Fly;
import pd.actors.mobs.pets.Snake;
import pd.messages.InlineText;
public class RandomEgg11 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg11.class)
			.t("name", "随机十一月灵魂")
			.t("desc", "召唤一个随机的十一月宠物，包括毒蛇、飞蝇、萤石粉蝶。");
	}
 public RandomEgg11() { super(Snake.class, Fly.class, ButterflyPet.class); } }
