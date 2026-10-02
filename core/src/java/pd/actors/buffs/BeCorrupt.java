/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class BeCorrupt extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BeCorrupt.class)
			.t("name", "侵蚀")
			.t("desc", "你被侵蚀了。侵蚀会阻止生命恢复，并将生命变化转化为额外伤害。剩余侵蚀效果：%s。");
	}

	private static final String LEVEL = "level";
	private static final String LAST_HP = "last_hp";
	private int level;
	private int lastHP;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override public boolean attachTo(Char target) { lastHP = target.HP; return super.attachTo(target); }
	public BeCorrupt level(int value) { level = Math.max(level, value); return this; }
	public int level() { return level; }

	@Override public boolean act() {
		if (!target.isAlive()) { detach(); return true; }
		if (target.HP > lastHP) {
			level -= target.HP - lastHP;
			target.HP = Math.max(1, lastHP - 1);
			lastHP = target.HP;
		} else if (target.HP < lastHP) {
			level--;
			target.HP = Math.max(1, target.HP - 1);
			lastHP = target.HP;
		}
		spend(TICK);
		if (level <= 0) detach();
		return true;
	}

	@Override public int icon() { return BuffIndicator.CORRUPT; }
	@Override public String desc() { return Messages.get(this, "desc", level); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(LEVEL, level); bundle.put(LAST_HP, lastHP); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); level = bundle.getInt(LEVEL); lastHP = bundle.getInt(LAST_HP); }
}
