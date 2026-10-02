/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Monkey; 
import pd.atlas.items.ConsumSummorDict;
import pd.messages.InlineText;
public class MonkeyEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MonkeyEgg.class)
			.t("name", "绿皮猴之魂")
			.t("desc", "召唤绿皮猴。");
	}


{image=ConsumSummorDict.MONKEY_EGG_0;}@Override protected LegacyPet hatchling(){return new Monkey();}@Override public int value(){return 500*quantity;}}
