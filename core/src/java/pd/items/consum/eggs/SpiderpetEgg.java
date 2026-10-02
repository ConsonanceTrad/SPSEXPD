/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Spider; 
import pd.atlas.items.ConsumSummorDict;
import pd.messages.InlineText;
public class SpiderpetEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SpiderpetEgg.class)
			.t("name", "植蛛之魂")
			.t("desc", "召唤植蛛。");
	}


{image=ConsumSummorDict.SPIDER_PET_EGG_0;}@Override protected LegacyPet hatchling(){return new Spider();}@Override public int value(){return 500*quantity;}}
