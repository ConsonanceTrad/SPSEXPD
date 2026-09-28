package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

abstract class SkillBook extends Item {
	static final String AC_READ = "READ";
	{ stackable = true; defaultAction = AC_READ; }
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero); actions.add(AC_READ); return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (!AC_READ.equals(action)) { super.execute(hero, action); return; }
		apply(hero);
		GLog.w(Messages.get(this, "skillup"));
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
	}
	abstract void apply(Hero hero);
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
