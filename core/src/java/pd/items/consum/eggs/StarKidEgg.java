/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.StarKid; 
import pd.atlas.items.ConsumSummorDict;
public class StarKidEgg extends Egg {{image=ConsumSummorDict.STAR_KID_EGG_0;}@Override protected LegacyPet hatchling(){return new StarKid();}@Override public int value(){return 500*quantity;}}
