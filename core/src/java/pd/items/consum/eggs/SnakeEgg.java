/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Snake; 
import pd.atlas.items.ConsumSummorDict;
import pd.messages.InlineText;
public class SnakeEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SnakeEgg.class)
			.t("name", "毒蛇之魂")
			.t("desc", "召唤毒蛇。");
	}


{image=ConsumSummorDict.SNAKE_PET_EGG_0;}@Override protected LegacyPet hatchling(){return new Snake();}@Override public int value(){return 500*quantity;}}
