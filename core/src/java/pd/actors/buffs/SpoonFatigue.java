/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.hero.Hero;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 银汤匙 / 圣餐银匙的「疲劳值」。
 *
 * <p>汤匙的充能是无限的，代价是每次用它都会累积疲劳；超过上限还继续用就会受伤。
 * 停下不用时疲劳会慢慢回落。</p>
 */
public class SpoonFatigue extends Buff {

	private static final String FATIGUE = "fatigue";
	private static final String CAP = "cap";
	private static final String COOLDOWN = "cooldown";

	/** 当前疲劳值。 */
	private int fatigue = 0;
	/** 疲劳上限。 */
	private int cap = 100;
	/** 施法后多少回合内不回落。 */
	private int cooldown = 0;

	/** 每次施法累积的疲劳。 */
	public static final int GAIN_PER_CAST = 12;
	/** 停止施法后不回落的时间。 */
	public static final int SETTLE_TURNS = 3;
	/** 超限时按最大生命的比例受伤。 */
	public static final float OVERLOAD_FRACTION = 0.10f;

	{
		type = buffType.NEGATIVE;
	}

	public SpoonFatigue setup(int cap) {
		this.cap = Math.max(1, cap);
		return this;
	}

	public int fatigue() {
		return fatigue;
	}

	public int cap() {
		return cap;
	}

	public boolean overloaded() {
		return fatigue > cap;
	}

	/** SPSEXPD: 累积一次施法疲劳；返回是否已超限。 */
	public boolean addFatigue() {
		fatigue += GAIN_PER_CAST;
		cooldown = SETTLE_TURNS;
		return overloaded();
	}

	@Override
	public boolean act() {
		if (cooldown > 0) {
			cooldown--;
		} else if (fatigue > 0) {
			fatigue--;
		}
		if (target instanceof Hero && fatigue <= 0 && cooldown <= 0) {
			detach();
			return true;
		}
		spend(TICK);
		return true;
	}

	@Override
	public String iconTextDisplay() {
		return fatigue + "/" + cap;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(FATIGUE, fatigue);
		bundle.put(CAP, cap);
		bundle.put(COOLDOWN, cooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		fatigue = bundle.getInt(FATIGUE);
		cap = bundle.getInt(CAP);
		cooldown = bundle.getInt(COOLDOWN);
	}
}
