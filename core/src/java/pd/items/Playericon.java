/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.utils.GLog;
import pd.messages.InlineText;

/** The original completion souvenir from the unfinished boss rush. */
public class Playericon extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Playericon.class)
			.t("name", "玩家图标")
			.t("thank4play", "感谢你参加BossRush测试，这是测试奖励。")
			.t("desc", "通过尚未完成的BossRush后得到的奖励，看起来和这场挑战一样粗糙。");
	}



	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
	}
	@Override public boolean doPickUp(Hero hero, int pos) {
		GLog.p(Messages.get(this, "thank4play"));
		return super.doPickUp(hero, pos);
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
