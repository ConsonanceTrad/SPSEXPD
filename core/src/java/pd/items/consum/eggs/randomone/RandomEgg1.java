package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.Datura;
import pd.actors.mobs.pets.DogPet;
import pd.actors.mobs.pets.Kodora;
import pd.messages.InlineText;
public class RandomEgg1 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg1.class)
			.t("name", "随机一月灵魂")
			.t("desc", "召唤一个随机的一月宠物，包括柯多拉、忠犬、曼陀罗。");
	}


 public RandomEgg1() { super(Kodora.class, DogPet.class, Datura.class); } }
