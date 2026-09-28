/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Stone; import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class StoneEgg extends Egg {{image=ItemSpriteSheet.STONE_PET_EGG;}@Override protected LegacyPet hatchling(){return new Stone();}@Override public int value(){return 500*quantity;}}
