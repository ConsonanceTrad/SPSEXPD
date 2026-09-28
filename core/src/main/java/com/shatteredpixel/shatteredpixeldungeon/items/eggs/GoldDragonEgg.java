/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.BugDragon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.GoldDragon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.Calendar;

public class GoldDragonEgg extends Egg {
	{
		image = ItemSpriteSheet.GOLD_DRAGON_EGG;
		moves = 2000; burns = freezes = poisons = lits = darks = lights = 20;
	}
	@Override protected LegacyPet hatchling() {
		return Calendar.getInstance().get(Calendar.MONTH) == Calendar.SEPTEMBER || Random.Int(50) == 0
				? new BugDragon() : new GoldDragon();
	}
	@Override public int value() { return 500 * quantity; }
}
