/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Stone; 
import pd.atlas.items.ConsumSummorDict;
import pd.messages.InlineText;
public class StoneEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneEgg.class)
			.t("name", "石拳石之魂")
			.t("desc", "召唤石拳石。");
	}
{image=ConsumSummorDict.STONE_PET_EGG_0;}@Override protected LegacyPet hatchling(){return new Stone();}@Override public int value(){return 500*quantity;}}
