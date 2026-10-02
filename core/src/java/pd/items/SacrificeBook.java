/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;

public class SacrificeBook extends Item {
	public static final String AC_USE = "USE";
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
		defaultAction = AC_USE;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (!AC_USE.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (Dungeon.sacrifice == 0) {
			hero.HTBoost += 5;
			hero.updateHT(true);
			if (hero.sprite != null) hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "use_1"));
		} else {
			hero.STR++;
			if (hero.sprite != null) hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "use_2"));
		}
		if (Dungeon.sacrifice > 1) {
			int minimum = 5 * Dungeon.sacrifice;
			int maximum = hero.permanentHT() / 5;
			int loss = maximum > minimum ? Random.Int(minimum, maximum) : minimum;
			hero.spendPermanentHT(Math.min(loss, hero.permanentHT() - 1));
			GLog.w(Messages.get(this, "use_lot"));
		}
		Dungeon.sacrifice++;
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
		hero.busy();
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
