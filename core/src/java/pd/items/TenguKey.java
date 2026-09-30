/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.Dungeon;
import pd.items.quest.AdventureJournal;
import pd.sprites.ItemSpriteSheet;

/** The prison boss's one-use portal to Tengu's hideout. */
public class TenguKey extends SpsBossKey {

	public static final String AC_PORT = SpsBossKey.AC_PORT;

	{
		image = ItemSpriteSheet.TENGU_KEY;
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
