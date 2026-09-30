/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Spider; import pd.sprites.ItemSpriteSheet;
public class SpiderpetEgg extends Egg {{image=ItemSpriteSheet.SPIDER_PET_EGG;}@Override protected LegacyPet hatchling(){return new Spider();}@Override public int value(){return 500*quantity;}}
