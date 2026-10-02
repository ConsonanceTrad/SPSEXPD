/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.Fly; import pd.actors.mobs.pets.LegacyPet; 
import pd.atlas.items.ConsumSummorDict;
public class FlyEgg extends Egg {{image=ConsumSummorDict.FLY_EGG_0;}@Override protected LegacyPet hatchling(){return new Fly();}@Override public int value(){return 500*quantity;}}
