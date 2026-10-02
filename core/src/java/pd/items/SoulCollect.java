/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificTaskDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.quest.AdventureJournal;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/** Zot's soul, used outside the prison to finish Otiluke's rescue. */
public class SoulCollect extends Item {

	public static final String AC_BREAK = "BREAK";

	{
		image = SpecificTaskDict.SOUL_COLLECT;
		stackable = false;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (Dungeon.branch == 0 && Dungeon.depth < 26) actions.add(AC_BREAK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_BREAK.equals(action)) {
			super.execute(hero, action);
			return;
		}
		GLog.w(Messages.get(this, "win"));
		AdventureJournal.complete(7);
		hero.sprite.operate(hero.pos);
		detach(hero.belongings.backpack);
		hero.spendAndNext(1f);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
