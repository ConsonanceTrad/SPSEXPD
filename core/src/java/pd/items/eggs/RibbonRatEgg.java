/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.RibbonRat; import pd.sprites.ItemSpriteSheet;
public class RibbonRatEgg extends Egg {{image=ItemSpriteSheet.RIBBON_RAT_EGG;}@Override protected LegacyPet hatchling(){return new RibbonRat();}@Override public int value(){return 500*quantity;}}
