package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.LitDemon;
import pd.actors.mobs.pets.Monkey;
import pd.actors.mobs.pets.Spider;
import pd.messages.InlineText;
public class RandomEgg12 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg12.class)
			.t("name", "随机十二月灵魂")
			.t("desc", "召唤一个随机的十二月宠物，包括链锯魔、植蛛、绿皮猴。");
	}
 public RandomEgg12() { super(LitDemon.class, Spider.class, Monkey.class); } }
