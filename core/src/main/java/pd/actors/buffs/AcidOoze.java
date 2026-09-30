/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.ui.BuffIndicator;
import render.utils.Random;

/** SPS-PD's permanent acid effect. Water is the only normal way to remove it. */
public class AcidOoze extends Buff {

	{
		type = buffType.NEGATIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.OOZE;
	}

	public static int tickDamage(int targetHT, int randomSix) {
		return randomSix == 0 ? 1 : Math.min(500, targetHT / 15);
	}

	@Override
	public boolean act() {
		if (target == null) return true;
		if (!target.isAlive()) {
			detach();
			return true;
		}

		target.damage(tickDamage(target.HT, Random.Int(6)), this);
		if (!target.isAlive() && target == Dungeon.hero) {
			Dungeon.fail(this);
		}
		spend(TICK);

		if (Dungeon.level != null && Dungeon.level.water[target.pos]) detach();
		target.needsIncomingDOTUpdate = true;
		return true;
	}

	@Override
	public void detach() {
		if (target != null) target.needsIncomingDOTUpdate = true;
		super.detach();
	}

}
