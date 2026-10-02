/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.StarKid; 
import pd.atlas.items.ConsumSummorDict;
import pd.messages.InlineText;
public class StarKidEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StarKidEgg.class)
			.t("name", "星芒之魂")
			.t("desc", "召唤星芒。");
	}


{image=ConsumSummorDict.STAR_KID_EGG_0;}@Override protected LegacyPet hatchling(){return new StarKid();}@Override public int value(){return 500*quantity;}}
