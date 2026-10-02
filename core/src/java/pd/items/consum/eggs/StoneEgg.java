/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Stone; 
import pd.atlas.items.ConsumSummorDict;
public class StoneEgg extends Egg {{image=ConsumSummorDict.STONE_PET_EGG_0;}@Override protected LegacyPet hatchling(){return new Stone();}@Override public int value(){return 500*quantity;}}
