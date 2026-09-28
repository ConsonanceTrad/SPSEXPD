/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.RibbonRat; import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class RibbonRatEgg extends Egg {{image=ItemSpriteSheet.RIBBON_RAT_EGG;}@Override protected LegacyPet hatchling(){return new RibbonRat();}@Override public int value(){return 500*quantity;}}
