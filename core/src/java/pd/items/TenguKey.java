/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.items.quest.AdventureJournal;
import pd.messages.InlineText;

/** The prison boss's one-use portal to Tengu's hideout. */
public class TenguKey extends SpsBossKey {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TenguKey.class)
			.t("name", "匿藏地传送门")
			.t("ac_port", "使用")
			.t("desc", "一张标有天狗头像的传送道具。没准它通往哪个地方。");
	}


	public static final String AC_PORT = SpsBossKey.AC_PORT;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	protected int destination() {
		return 10;
	}

	@Override
	protected boolean bossKilled() {
		if (Dungeon.tenguDenKilled) return true;
		if (Dungeon.hero == null) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		return journal != null && journal.isCompleted(destination());
	}
}
