/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.RibbonRat; 
import pd.atlas.items.ConsumSummorDict;
public class RibbonRatEgg extends Egg {{image=ConsumSummorDict.RIBBON_RAT_EGG_0;}@Override protected LegacyPet hatchling(){return new RibbonRat();}@Override public int value(){return 500*quantity;}}
