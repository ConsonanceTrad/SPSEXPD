package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.FoxHelper;
import pd.actors.mobs.pets.GentleCrab;
import pd.actors.mobs.pets.Stone;
import pd.messages.InlineText;
public class RandomEgg2 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg2.class)
			.t("name", "随机二月灵魂")
			.t("desc", "召唤一个随机的二月宠物，包括绅士蟹、石拳石、狐女仆。");
	}
 public RandomEgg2() { super(GentleCrab.class, Stone.class, FoxHelper.class); } }
