/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class LevelDown extends Item {

	public static final String AC_USE = "USE";
	{
		image = ItemSpriteSheet.ORE;
		stackable = true;
		defaultAction = AC_USE;
	}

	@Override public ArrayList<String> actions(Hero hero) { ArrayList<String> actions = super.actions(hero); actions.add(AC_USE); return actions; }
	@Override public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_USE.equals(action)) return;
		reduceLevel(hero);
		detach(hero.belongings.backpack);
		hero.sprite.centerEmitter().start(Speck.factory(Speck.DOWN), 0.05f, 10);
		hero.spendAndNext(Actor.TICK);
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }

	public static boolean reduceLevel(Hero hero) {
		if (hero.lvl <= 1) return false;
		hero.lvl--;
		hero.exp = Math.min(hero.exp, hero.maxExp() - 1);
		hero.updateHT(false);
		return true;
	}
}
