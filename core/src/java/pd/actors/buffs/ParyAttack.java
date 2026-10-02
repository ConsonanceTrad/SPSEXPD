/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** Legacy stationary parry which gains one stack per turn, or two after level 55. */
public class ParyAttack extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ParyAttack.class)
			.t("name", "原地格挡")
			.t("desc", "保持原地会逐回合提高攻击并降低所受伤害；移动或金币不足时状态结束。当前强度：%s%%。");
	}



	private static final String LEVEL = "level";
	private static final String POS = "pos";
	private int level;
	private int pos;
	{ type = buffType.POSITIVE; announced = true; }
	@Override public boolean attachTo(Char target) { pos = target.pos; return super.attachTo(target); }
	@Override public boolean act() {
		if (!target.isAlive() || target.pos != pos || Dungeon.gold < level * 10) {
			detach();
			return true;
		}
		level += Dungeon.hero != null && Dungeon.hero.lvl > 55 ? 2 : 1;
		if (level > 100) Dungeon.gold = Math.max(0, Dungeon.gold - level * 10);
		spend(TICK);
		return true;
	}
	public int level() { return level; }
	public ParyAttack level(int value) { level = Math.max(level, value); return this; }
	@Override public int icon() { return BuffIndicator.BLESS; }
	@Override public String iconTextDisplay() { return Integer.toString(level); }
	@Override public String desc() { return Messages.get(this, "desc", Messages.decimalFormat("#.##", level / 2.5f)); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(POS, pos); bundle.put(LEVEL, level); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); pos = bundle.getInt(POS); level = Math.max(0, bundle.getInt(LEVEL)); }
}
