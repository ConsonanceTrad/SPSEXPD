/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.FrogPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class FrogpetEgg extends Egg { { image = ItemSpriteSheet.FROG_PET_EGG; } @Override protected LegacyPet hatchling() { return new FrogPet(); } @Override public int value() { return 500 * quantity; } }
