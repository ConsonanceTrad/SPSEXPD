/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;

import java.util.ArrayList;

public class BugMeat extends Food {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 50;
		hornValue = 1;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		if (hero.HT > 1) {
			hero.HTBoost--;
			hero.updateHT(false);
		}
	}

	@Override public int value() { return 350 * quantity; }
}
