/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.ui.BuffIndicator;
import render.utils.Bundle;

/** SPS frostbite: slows movement and deals percentage damage when the target moves. */
public class FrostIce extends Buff implements Buff.DOTbuff {

	private static final String LEFT = "left";
	private static final String POS = "pos";

	private int pos;
	private float left;
	private boolean first;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean attachTo(Char target) {
		first = true;
		pos = target.pos;
		return super.attachTo(target);
	}

	public static int initialDamage(int targetHT) {
		return Math.min(1000, targetHT / 30);
	}

	public static int movementDamage(int targetHT) {
		return Math.min(500, targetHT / 30);
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
			if (first) {
				target.damage(initialDamage(target.HT), this);
				first = false;
			}
			if (target.pos != pos) {
				pos = target.pos == -1 ? 0 : target.pos;
				target.damage(movementDamage(target.HT), this);
			}
			Buff.detach(target, Burning.class);
		} else {
			detach();
		}

		spend(TICK);
		left -= TICK;
		if (left <= 0) detach();
		target.needsIncomingDOTUpdate = true;
		return true;
	}

	public void level(int value) {
		if (left < value) left = value;
		if (target != null) target.needsIncomingDOTUpdate = true;
	}

	public float level() {
		return left;
	}

	@Override
	public int icon() {
		return BuffIndicator.FROST;
	}

	@Override
	public String desc() {
		return pd.messages.Messages.get(this, "desc", left);
	}

	@Override
	public int totalIncomingDMG() {
		return Math.max(0, movementDamage(target.HT));
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(POS, pos);
		bundle.put(LEFT, left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		pos = bundle.getInt(POS);
		left = bundle.getFloat(LEFT);
	}
}
