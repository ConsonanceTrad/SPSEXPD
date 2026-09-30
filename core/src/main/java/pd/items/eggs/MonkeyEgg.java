/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.Monkey; import pd.sprites.ItemSpriteSheet;
public class MonkeyEgg extends Egg {{image=ItemSpriteSheet.MONKEY_EGG;}@Override protected LegacyPet hatchling(){return new Monkey();}@Override public int value(){return 500*quantity;}}
