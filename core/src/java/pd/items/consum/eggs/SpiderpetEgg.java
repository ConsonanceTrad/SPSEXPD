/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Spider; 
import pd.atlas.items.ConsumSummorDict;
public class SpiderpetEgg extends Egg {{image=ConsumSummorDict.SPIDER_PET_EGG_0;}@Override protected LegacyPet hatchling(){return new Spider();}@Override public int value(){return 500*quantity;}}
