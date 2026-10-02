package pd.items.consum.eggs.randomone;
import pd.actors.mobs.pets.Chocobo;
import pd.actors.mobs.pets.Datura;
import pd.actors.mobs.pets.RibbonRat;
import pd.messages.InlineText;
public class RandomEgg9 extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEgg9.class)
			.t("name", "随机九月灵魂")
			.t("desc", "召唤一个随机的九月宠物，包括缎带鼠、陆行鸟、曼陀罗。");
	}


 public RandomEgg9() { super(RibbonRat.class, Chocobo.class, Datura.class); } }
