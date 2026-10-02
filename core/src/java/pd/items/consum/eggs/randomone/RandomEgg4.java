package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.Fly;
import pd.actors.mobs.pets.Kodora;
import pd.actors.mobs.pets.Monkey;
import pd.messages.InlineText;
public class RandomEgg4 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg4.class)
			.t("name", "随机四月灵魂")
			.t("desc", "召唤一个随机的四月宠物，包括柯多拉、飞蝇、绿皮猴。");
	}


 public RandomEgg4() { super(Kodora.class, Fly.class, Monkey.class); } }
