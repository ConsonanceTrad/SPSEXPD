/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Monkey; 
import pd.atlas.items.ConsumSummorDict;
public class MonkeyEgg extends Egg {{image=ConsumSummorDict.MONKEY_EGG_0;}@Override protected LegacyPet hatchling(){return new Monkey();}@Override public int value(){return 500*quantity;}}
