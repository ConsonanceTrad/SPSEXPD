/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.items.quest.AdventureJournal;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The original SPS journal reader, backed by the migrated adventure journal. */
public class KnowledgeBook extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(KnowledgeBook.class)
			.t("name", "知识之书")
			.t("desc", "记录了许多异常地点的书。阅读后会打开异界日志。")
			.t("ac_read", "阅读");
	}

	public static final String AC_READ = "READ";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_READ;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_READ);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_READ.equals(action)) {
			AdventureJournal journal = hero.belongings.getItem(AdventureJournal.class);
			if (journal == null) GLog.w(Messages.get(AdventureJournal.class, "missing"));
			else journal.execute(hero, AdventureJournal.AC_READ);
		} else {
			super.execute(hero, action);
		}
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 5000 * quantity; }
}
