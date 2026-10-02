/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** Soldier armor which absorbs all damage and also decays by one point per turn. */
public class MechArmor extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MechArmor.class)
			.t("name", "机械护甲")
			.t("desc", "机械护甲会吸收全部伤害，但每回合也会损失1点。剩余护甲：%s。");
	}

	private static final String LEVEL = "level";
	private int level;
	{ type = buffType.POSITIVE; announced = true; }
	public MechArmor level(int value) { level = Math.max(level, value); return this; }
	public int level() { return level; }
	public int absorb(int damage) {
		int absorbed = Math.min(level, Math.max(0, damage));
		level -= absorbed;
		if (level <= 0) detachLinkedArmor();
		return damage - absorbed;
	}
	@Override public boolean act() {
		if (!target.isAlive()) {
			detach();
			return true;
		}
		level--;
		if (level <= 1) detachLinkedArmor();
		else spend(TICK);
		return true;
	}
	private void detachLinkedArmor() {
		if (target != null) {
			Buff.detach(target, ShieldArmor.class);
			Buff.detach(target, MagicArmor.class);
			Buff.detach(target, EnergyArmor.class);
		}
		detach();
	}
	@Override public int icon() { return BuffIndicator.ARMOR; }
	@Override public String iconTextDisplay() { return Integer.toString(level); }
	@Override public String desc() { return Messages.get(this, "desc", level); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(LEVEL, level); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); level = Math.max(0, bundle.getInt(LEVEL)); }
}
