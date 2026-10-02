package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.PigPet;
import pd.actors.mobs.pets.StarKid;
import pd.actors.mobs.pets.Stone;
import pd.messages.InlineText;
public class RandomEgg10 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg10.class)
			.t("name", "随机十月灵魂")
			.t("desc", "召唤一个随机的十月宠物，包括星芒、石拳石、像素猪。");
	}
 public RandomEgg10() { super(StarKid.class, Stone.class, PigPet.class); } }
