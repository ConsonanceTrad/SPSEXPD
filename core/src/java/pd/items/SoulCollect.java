/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificTaskDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.quest.AdventureJournal;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;
import pd.messages.InlineText;

/** Zot's soul, used outside the prison to finish Otiluke's rescue. */
public class SoulCollect extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SoulCollect.class)
			.t("name", "灵魂囚禁石")
			.t("desc", "难以想象Otiluke竟会被这种东西困住。破坏它就能救出他。")
			.t("ac_break", "破坏")
			.t("win", "谢谢你。虽然还不知道你是谁，但我们小镇见吧。");
	}




	public static final String AC_BREAK = "BREAK";

	{
		image = SpecificTaskDict.SOUL_COLLECT;
		stackable = false;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (Dungeon.branch == 0 && Dungeon.depth < 40) actions.add(AC_BREAK);
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
