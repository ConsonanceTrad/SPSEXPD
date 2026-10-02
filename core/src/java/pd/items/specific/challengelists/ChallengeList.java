/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.challengelists;

import pd.Statistics;
import pd.items.Item;

public abstract class ChallengeList extends Item {
	{
		stackable = false;
		unique = true;
	}
	public abstract int challenge();
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() {
		if (challenge() < 5) return 0;
		if (Statistics.deepestFloor < 26) return 9000 * quantity;
		return (challenge() == 5 ? 300 : challenge() == 6 ? 500 : 600) * quantity;
	}
}
