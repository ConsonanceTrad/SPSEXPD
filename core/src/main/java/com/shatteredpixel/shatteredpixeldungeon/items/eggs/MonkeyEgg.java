/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Monkey; import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class MonkeyEgg extends Egg {{image=ItemSpriteSheet.MONKEY_EGG;}@Override protected LegacyPet hatchling(){return new Monkey();}@Override public int value(){return 500*quantity;}}
