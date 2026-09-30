/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.math.Random;

public class BloodImbue extends FlavourBuff {
	{
		type = buffType.POSITIVE;
		announced = true;
		immunities.add(Paralysis.class);
		immunities.add(Roots.class);
		immunities.add(Slow.class);
		immunities.add(Bleeding.class);
		immunities.add(STRDown.class);
	}
	public void proc(Char enemy) {
		switch (Random.Int(4)) {
			case 0: Buff.prolong(enemy, Cripple.class, 3f); break;
			case 1: Buff.prolong(enemy, Roots.class, 3f); break;
			case 2: Buff.prolong(enemy, Paralysis.class, 3f); break;
			default: break;
		}
	}
	@Override public int icon() { return BuffIndicator.IMBUE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
