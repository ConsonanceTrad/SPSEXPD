/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Kodora;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class KodoraEgg extends Egg { { image = ItemSpriteSheet.KODORA_EGG; } @Override protected LegacyPet hatchling() { return new Kodora(); } @Override public int value() { return 500 * quantity; } }
