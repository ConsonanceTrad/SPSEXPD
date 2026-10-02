/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.challengelists;

import pd.Statistics;
import pd.items.Item;
import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;

public abstract class ChallengeList extends Item {
	{
		image = SpecificTaskDict.TELEPORT_COORDINATE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ChallengeList.class)
			.t("name", "挑战纸片")
			.t("desc", "原先的传送道具，但已经丧失了它原有的魔力。把它加入挑战日志即可恢复对应地点的记录。");
	}



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
