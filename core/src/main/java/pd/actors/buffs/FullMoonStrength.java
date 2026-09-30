/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Statistics;
import watabou.utils.Bundle;

import java.util.Calendar;

public class FullMoonStrength extends Buff {

	private static final String HITS = "hits";

	private int hits = hitsFor(isNightNow(), Statistics.deepestFloor);

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public static int hitsFor(boolean night, int deepestFloor) {
		return deepestFloor / 5 + (night ? 9 : 3);
	}

	private static boolean isNightNow() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		return hour > 19 || hour < 7;
	}

	public FullMoonStrength setHits(int value) {
		hits = Math.max(1, value);
		return this;
	}

	public int hits() {
		return hits;
	}

	@Override
	public void detach() {
		if (--hits <= 0) super.detach();
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
