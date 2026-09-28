/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.StarKid; import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class StarKidEgg extends Egg {{image=ItemSpriteSheet.STAR_KID_EGG;}@Override protected LegacyPet hatchling(){return new StarKid();}@Override public int value(){return 500*quantity;}}
