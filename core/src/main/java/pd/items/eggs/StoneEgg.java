/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Stone; import pd.sprites.ItemSpriteSheet;
public class StoneEgg extends Egg {{image=ItemSpriteSheet.STONE_PET_EGG;}@Override protected LegacyPet hatchling(){return new Stone();}@Override public int value(){return 500*quantity;}}
