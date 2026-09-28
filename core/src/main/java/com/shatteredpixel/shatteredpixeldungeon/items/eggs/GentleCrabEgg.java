/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.GentleCrab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class GentleCrabEgg extends Egg { { image = ItemSpriteSheet.GENTLE_CRAB_EGG; } @Override protected LegacyPet hatchling() { return new GentleCrab(); } @Override public int value() { return 500 * quantity; } }
