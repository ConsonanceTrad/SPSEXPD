/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Forces the clockwork scarecrow to attack only adjacent targets. */
public class Locked extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Locked.class)
			.t("name", "锁闭")
			.t("desc", "一把无形的锁锁死了你的背包，是你无法使用消耗类道具，持续%s回合。");
	}



	{
		type = buffType.NEGATIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.LOCKED_FLOOR;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
