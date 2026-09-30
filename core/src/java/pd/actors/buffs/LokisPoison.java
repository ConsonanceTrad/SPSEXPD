/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

/** The relic flail's stronger poison, which deals damage from half its remaining duration. */
public class LokisPoison extends Poison {
	@Override public void set(float duration) {
		left = duration;
		if (target != null) target.needsIncomingDOTUpdate = true;
	}
	@Override public boolean act() {
		if (target.isAlive()) {
			target.damage((int)(left / 2f) + 1, this);
			spend(TICK);
			if ((left -= TICK) <= 0) detach();
			else target.needsIncomingDOTUpdate = true;
		} else {
			detach();
		}
		return true;
	}
}
