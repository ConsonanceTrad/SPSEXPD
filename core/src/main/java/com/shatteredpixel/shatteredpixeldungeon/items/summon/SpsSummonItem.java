package com.shatteredpixel.shatteredpixeldungeon.items.summon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

abstract class SpsSummonItem extends Item {

	static final String AC_ACTIVE = "ACTIVE";

	{
		defaultAction = AC_ACTIVE;
		usesTargeting = true;
		stackable = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ACTIVE);
		return actions;
	}

	void beginActivation(Hero hero) {
		super.execute(hero, AC_THROW);
	}

	void summonOrRecover(int target, Mob summon) {
		int cell = summonCell(target);
		if (cell == -1) {
			Dungeon.level.drop(this, Dungeon.hero.pos).sprite.drop();
			return;
		}
		summon.pos = cell;
		summon.state = summon.HUNTING;
		GameScene.add(summon, 1f);
	}

	protected static int summonCell(int target) {
		if (valid(target)) return target;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = target + offset;
			if (valid(cell)) candidates.add(cell);
		}
		return candidates.isEmpty() ? -1 : Random.element(candidates);
	}

	private static boolean valid(int cell) {
		return cell >= 0 && cell < Dungeon.level.length()
				&& Dungeon.level.passable[cell] && !Dungeon.level.pit[cell]
				&& Actor.findChar(cell) == null;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 100 * quantity;
	}
}
