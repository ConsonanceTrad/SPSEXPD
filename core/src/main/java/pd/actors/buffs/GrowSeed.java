/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** A short SPS damage-over-time growth which feeds nearby living characters. */
public class GrowSeed extends Buff implements Hero.Doom {

	private static final String LEFT = "left";
	private float left;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean act() {
		if (!target.isAlive()) {
			detach();
			return true;
		}
		int damage = Random.IntRange(1, Math.max(1, target.HT / 20));
		target.damage(damage, Bleeding.class);
		if (Dungeon.level != null) {
			for (int offset : PathFinder.NEIGHBOURS8) {
				int cell = target.pos + offset;
				if (cell < 0 || cell >= Dungeon.level.length()) continue;
				Char neighbour = Actor.findChar(cell);
				if (neighbour == null || neighbour == target || !neighbour.isAlive()) continue;
				int missing = neighbour.HT - neighbour.HP;
				if (missing > 0) neighbour.HP += Random.IntRange(1, Math.min(damage, missing));
			}
		}
		spend(TICK);
		if ((left -= TICK) <= 0) detach();
		return true;
	}

	public void set(float duration) { left = Math.max(0, duration); }
	public float level() { return left; }
	public void level(int value) { if (left < value) left = value; }

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = Math.max(0, bundle.getFloat(LEFT));
	}
	@Override public int icon() { return BuffIndicator.BLEEDING; }
	@Override public String iconTextDisplay() { return Integer.toString((int)left); }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns(left)); }
	@Override public String heroMessage() { return Messages.get(this, "heromsg"); }
	@Override public void onDeath() { Dungeon.fail(this); }
}
