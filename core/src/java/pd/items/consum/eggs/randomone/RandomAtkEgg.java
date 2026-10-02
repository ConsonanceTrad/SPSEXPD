/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.actors.mobs.pets.GentleCrab;
import pd.actors.mobs.pets.Kodora;
import pd.actors.mobs.pets.RibbonRat;
import pd.actors.mobs.pets.Snake;
import pd.messages.InlineText;

public class RandomAtkEgg extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomAtkEgg.class)
			.t("name", "随机战斗灵魂")
			.t("desc", "召唤一个随机的战斗宠物。");
	}



	public RandomAtkEgg() { super(Kodora.class, Snake.class, RibbonRat.class, GentleCrab.class); }
}
