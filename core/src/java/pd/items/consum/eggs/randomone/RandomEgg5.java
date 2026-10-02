package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.Chocobo;
import pd.actors.mobs.pets.PigPet;
import pd.actors.mobs.pets.Snake;
import pd.messages.InlineText;
public class RandomEgg5 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg5.class)
			.t("name", "随机五月灵魂")
			.t("desc", "召唤一个随机的五月宠物，包括毒蛇、陆行鸟、像素猪。");
	}
 public RandomEgg5() { super(Snake.class, Chocobo.class, PigPet.class); } }
