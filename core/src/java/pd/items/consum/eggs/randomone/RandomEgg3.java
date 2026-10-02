package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.DwarfBoy;
import pd.actors.mobs.pets.FrogPet;
import pd.actors.mobs.pets.RibbonRat;
import pd.messages.InlineText;
public class RandomEgg3 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg3.class)
			.t("name", "随机三月灵魂")
			.t("desc", "召唤一个随机的三月宠物，包括缎带鼠、矮人学徒、呆头蛙。");
	}


 public RandomEgg3() { super(RibbonRat.class, DwarfBoy.class, FrogPet.class); } }
