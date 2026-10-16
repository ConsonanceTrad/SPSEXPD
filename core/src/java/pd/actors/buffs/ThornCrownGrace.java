/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.hero.Hero;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 荆棘冠冕的「苦行」计数——每受到一次伤害就记一次，
 * 满 21 次时驱散身上的负面状态与诅咒，然后从头计起。
 */
public class ThornCrownGrace extends Buff {

	private static final String HITS = "hits";

	/** 需要累计的受击次数。 */
	public static final int HITS_REQUIRED = 21;

	private int hits = 0;

	{
		type = buffType.POSITIVE;
	}

	public void addHit() {
		hits++;
	}

	public boolean ready() {
		return hits >= HITS_REQUIRED;
	}

	public void reset() {
		hits = 0;
	}

	public int hits() {
		return hits;
	}

	@Override
	public boolean act() {
		spend(TICK);
		return true;
	}

	@Override
	public String iconTextDisplay() {
		return hits + "/" + HITS_REQUIRED;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HITS, hits);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		hits = bundle.getInt(HITS);
	}
}
