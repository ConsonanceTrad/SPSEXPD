/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LitDemon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class LitDemonEgg extends Egg { { image = ItemSpriteSheet.LIT_DEMON_EGG; } @Override protected LegacyPet hatchling() { return new LitDemon(); } @Override public int value() { return 500 * quantity; } }
