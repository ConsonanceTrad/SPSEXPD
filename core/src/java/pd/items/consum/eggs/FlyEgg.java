/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.Fly; import pd.actors.mobs.pets.LegacyPet; 
import pd.atlas.items.ConsumSummorDict;
import pd.messages.InlineText;
public class FlyEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FlyEgg.class)
			.t("name", "飞蝇之魂")
			.t("desc", "召唤飞蝇。");
	}


{image=ConsumSummorDict.FLY_EGG_0;}@Override protected LegacyPet hatchling(){return new Fly();}@Override public int value(){return 500*quantity;}}
