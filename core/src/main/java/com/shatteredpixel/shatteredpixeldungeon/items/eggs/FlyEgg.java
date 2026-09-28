/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Fly; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet; import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class FlyEgg extends Egg {{image=ItemSpriteSheet.FLY_EGG;}@Override protected LegacyPet hatchling(){return new Fly();}@Override public int value(){return 500*quantity;}}
