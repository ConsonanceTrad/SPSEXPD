package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.DwarfBoy;
import pd.actors.mobs.pets.FrogPet;
import pd.actors.mobs.pets.GentleCrab;
import pd.messages.InlineText;
public class RandomEgg7 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg7.class)
			.t("name", "随机七月灵魂")
			.t("desc", "召唤一个随机的七月宠物，包括绅士蟹、矮人学徒、呆头蛙。");
	}


 public RandomEgg7() { super(GentleCrab.class, DwarfBoy.class, FrogPet.class); } }
