/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class AttackUp extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(AttackUp.class)
			.t("name", "攻击提升")
			.t("desc", "剩余%1$s回合，物理攻击伤害提高%2$s%%。");
	}

	private static final String LEVEL = "level";
	private static final String LEGACY_LEFT = "left";
	private int level;
	{ type = buffType.POSITIVE; announced = true; }
	public AttackUp level(int value) { level = Math.max(level, value); return this; }
	public void set(float duration) { postpone(Math.max(0, duration)); }
	public int level() { return level; }
	public float damageMultiplier() { return 1f + level / 100f; }
	@Override public int icon() { return BuffIndicator.FURY; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns(), level); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(LEVEL, level); bundle.put(LEGACY_LEFT, Math.max(0, cooldown())); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); level = bundle.getInt(LEVEL); if (bundle.contains(LEGACY_LEFT)) postpone(Math.max(0, bundle.getFloat(LEGACY_LEFT))); }
}
