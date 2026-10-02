/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.messages.InlineText;

/** Converts into one turn of SPS glass shielding on the following actor tick. */
public class DelayProtect extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DelayProtect.class)
			.t("name", "延时保护")
			.t("desc", "效果结束时获得一层玻璃保护。\n\n剩余时间：%s回合。");
	}

	@Override
	public boolean attachTo(pd.actors.Char target) {
		if (!super.attachTo(target)) return false;
		if (cooldown() == 0) spend(TICK);
		return true;
	}

	@Override
	public boolean act() {
		Buff.affect(target, GlassShield.class).turns(1);
		detach();
		return true;
	}
	@Override public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
}
