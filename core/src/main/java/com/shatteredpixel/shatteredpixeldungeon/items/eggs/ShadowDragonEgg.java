/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.ShadowDragon;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** Guaranteed shadow-dragon soul from the original dragon cave. */
public class ShadowDragonEgg extends Egg {
	{
		image = ItemSpriteSheet.SHADOW_DRAGON_EGG;
		lights = 20;
	}

	@Override protected LegacyPet hatchling() { return new ShadowDragon(); }

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		boolean pickedUp = super.doPickUp(hero, pos);
		if (pickedUp && Dungeon.branch == AdventureJournal.branchFor(17)) {
			AdventureJournal.complete(17);
		}
		return pickedUp;
	}

	@Override public int value() { return 500 * quantity; }
}
