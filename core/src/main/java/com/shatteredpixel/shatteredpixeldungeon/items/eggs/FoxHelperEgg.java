/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.FoxHelper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class FoxHelperEgg extends Egg { { image = ItemSpriteSheet.FOX_HELPER_EGG; } @Override protected LegacyPet hatchling() { return new FoxHelper(); } @Override public int value() { return 500 * quantity; } }
