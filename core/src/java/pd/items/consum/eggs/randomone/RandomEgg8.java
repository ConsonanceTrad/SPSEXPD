package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.DogPet;
import pd.actors.mobs.pets.FoxHelper;
import pd.actors.mobs.pets.StarKid;
import pd.messages.InlineText;
public class RandomEgg8 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg8.class)
			.t("name", "随机八月灵魂")
			.t("desc", "召唤一个随机的八月宠物，包括星芒、忠犬、狐女仆。");
	}
 public RandomEgg8() { super(StarKid.class, DogPet.class, FoxHelper.class); } }
