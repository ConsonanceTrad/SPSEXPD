/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.ShadowDragon;
import pd.items.quest.AdventureJournal;
import pd.sprites.ItemSpriteSheet;

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
