/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class Vialupdater extends Item {
	public static final String AC_USE = "USE";
	{
		image = ItemSpriteSheet.VIAL_UPDATER;
		stackable = false;
		unique = true;
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
		curUser = hero;
		detach(hero.belongings.backpack);
		Dungeon.dewWater = true;
		Dungeon.wings = true;
		if (hero.sprite != null) hero.sprite.centerEmitter().start(Speck.factory(Speck.UP), 0.05f, 10);
		hero.spendAndNext(1f);
		hero.busy();
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
