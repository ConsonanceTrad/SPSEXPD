/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Snake; import pd.sprites.ItemSpriteSheet;
public class SnakeEgg extends Egg {{image=ItemSpriteSheet.SNAKE_PET_EGG;}@Override protected LegacyPet hatchling(){return new Snake();}@Override public int value(){return 500*quantity;}}
