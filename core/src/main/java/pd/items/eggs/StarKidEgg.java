/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs; import pd.actors.mobs.pets.LegacyPet; import pd.actors.mobs.pets.StarKid; import pd.sprites.ItemSpriteSheet;
public class StarKidEgg extends Egg {{image=ItemSpriteSheet.STAR_KID_EGG;}@Override protected LegacyPet hatchling(){return new StarKid();}@Override public int value(){return 500*quantity;}}
