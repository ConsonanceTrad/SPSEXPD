/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Snake; 
import pd.atlas.items.ConsumSummorDict;
public class SnakeEgg extends Egg {{image=ConsumSummorDict.SNAKE_PET_EGG_0;}@Override protected LegacyPet hatchling(){return new Snake();}@Override public int value(){return 500*quantity;}}
