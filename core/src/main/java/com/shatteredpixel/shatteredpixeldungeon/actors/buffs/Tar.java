/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Legacy tar: keeps burning active and is washed away by water. */
public class Tar extends Buff {
	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
			Burning burning = target.buff(Burning.class);
			if (burning != null) burning.reignite(target, 3f);
		}
		if (Dungeon.level != null && Dungeon.level.water[target.pos] && !target.flying) detach();
		else spend(TICK);
		return true;
	}

	@Override public int icon() { return BuffIndicator.OOZE; }
	@Override public String heroMessage() { return Messages.get(this, "heromsg"); }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
