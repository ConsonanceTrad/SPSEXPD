/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.RibbonRat; 
import pd.atlas.items.ConsumSummorDict;
import pd.messages.InlineText;
public class RibbonRatEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RibbonRatEgg.class)
			.t("name", "缎带鼠之魂")
			.t("desc", "召唤缎带鼠。");
	}


{image=ConsumSummorDict.RIBBON_RAT_EGG_0;}@Override protected LegacyPet hatchling(){return new RibbonRat();}@Override public int value(){return 500*quantity;}}
