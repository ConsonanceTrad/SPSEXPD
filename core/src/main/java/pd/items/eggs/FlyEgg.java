/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs; import pd.actors.mobs.pets.Fly; import pd.actors.mobs.pets.LegacyPet; import pd.sprites.ItemSpriteSheet;
public class FlyEgg extends Egg {{image=ItemSpriteSheet.FLY_EGG;}@Override protected LegacyPet hatchling(){return new Fly();}@Override public int value(){return 500*quantity;}}
