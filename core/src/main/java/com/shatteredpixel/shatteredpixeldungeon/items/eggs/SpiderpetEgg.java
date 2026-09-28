/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Spider; import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class SpiderpetEgg extends Egg {{image=ItemSpriteSheet.SPIDER_PET_EGG;}@Override protected LegacyPet hatchling(){return new Spider();}@Override public int value(){return 500*quantity;}}
