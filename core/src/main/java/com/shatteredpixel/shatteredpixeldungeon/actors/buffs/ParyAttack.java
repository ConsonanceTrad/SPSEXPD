/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

/** Legacy stationary parry which gains one stack per turn, or two after level 55. */
public class ParyAttack extends Buff {
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
