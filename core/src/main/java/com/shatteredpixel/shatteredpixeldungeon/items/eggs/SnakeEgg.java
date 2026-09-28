/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet; import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Snake; import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class SnakeEgg extends Egg {{image=ItemSpriteSheet.SNAKE_PET_EGG;}@Override protected LegacyPet hatchling(){return new Snake();}@Override public int value(){return 500*quantity;}}
