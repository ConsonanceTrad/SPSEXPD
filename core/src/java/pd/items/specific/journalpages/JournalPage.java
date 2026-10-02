/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.journalpages;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.items.Heap;
import pd.items.Item;
import pd.messages.InlineText;

public class JournalPage extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(JournalPage.class)
			.t("name", "坐标")
			.t("desc", "可以收录进冒险日志的地点坐标。");
	}

	private final int destination;
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = false;
		unique = true;
	}
	protected JournalPage(int destination) {
		this.destination = destination;
	}
	public int destination() { return destination; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 150 * quantity; }

	public static void dropAt(JournalPage page, int cell) {
		if (page == null || Dungeon.level == null || cell < 0 || cell >= Dungeon.level.length()) return;
		Heap heap = Dungeon.level.drop(page, cell);
		if (heap != null && heap.sprite != null) heap.sprite.drop();
	}
}
