/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.hero.Hero;
import pd.items.consum.scrolls.ScrollOfRecharging;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 精致/反转怀表带来的「时间加速」状态。
 *
 * <p>持续期间每回合为英雄身上的所有法杖额外补充能量（相当于法杖充能加速），
 * 移动加速由同时施加的 {@link Haste} 负责。</p>
 */
public class SpsTimeWarp extends Buff {

	private static final String LEFT = "left";

	private int left = 0;

	{
		type = buffType.POSITIVE;
	}

	public void setTurns(int turns) {
		this.left = Math.max(this.left, turns);
	}

	public int turnsLeft() {
		return left;
	}

	@Override
	public boolean act() {
		if (left > 0 && target instanceof Hero) {
			ScrollOfRecharging.charge((Hero) target);
		}
		left--;
		spend(TICK);
		if (left <= 0) {
			detach();
		}
		return true;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getInt(LEFT);
	}
}
