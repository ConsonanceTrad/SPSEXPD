/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.Bundle;

/** Grants permanent combat growth after the requested number of hostile kills. */
public class LearnSkill extends Buff {
	private static final String LEFT = "left";
	private int left;
	{ type = buffType.POSITIVE; announced = true; }
	public LearnSkill set(int value) { left = Math.max(left, value); return this; }
	public int left() { return left; }
	public void onKill() {
		if (left > 0) left--;
		if (left <= 0 && target instanceof Hero) {
			Hero hero = (Hero) target;
			int gain = hero.lvl > 55 ? 2 : 1;
			hero.improveAttackSkill(gain);
			hero.improveDefenseSkill(gain);
			hero.improveMagicSkill(gain);
			hero.HTBoost += gain;
			hero.updateHT(true);
			detach();
		}
	}
	@Override public boolean act() { spend(TICK); return true; }
	@Override public int icon() { return BuffIndicator.BLESS; }
	@Override public String iconTextDisplay() { return Integer.toString(left); }
	@Override public String desc() { return Messages.get(this, "desc", left); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(LEFT, left); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); left = Math.max(0, bundle.getInt(LEFT)); }
}
