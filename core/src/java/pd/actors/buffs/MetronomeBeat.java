/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.hero.Hero;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 节拍器——「踩拍」计数。
 *
 * <p>用节拍器指定一个_可抵达_的格子作为「下一拍」（陷阱格、虚空格都可以，因为踩上去只是有后果）；
 * 只要你下一步真的落到那一格上就算踩中，连击 +1；没踩中则清零。
 * 连击满 4 时触发一次奖励：不消耗回合的行动 + 护盾_或_强化攻击（模式由节拍器决定）。</p>
 */
public class MetronomeBeat extends Buff {

	private static final String BEATS = "beats";
	private static final String TARGET = "target";
	private static final String MODE = "mode";

	/** 触发所需的连击数。 */
	public static final int BEATS_REQUIRED = 4;

	/** 奖励模式：护盾。 */
	public static final int MODE_SHIELD = 0;
	/** 奖励模式：强化攻击。 */
	public static final int MODE_POWER = 1;

	private int beats = 0;
	/** 下一拍格（-1 = 还没指定）。 */
	private int target = -1;
	private int mode = MODE_SHIELD;

	{
		type = buffType.POSITIVE;
	}

	/** SPSEXPD: 这一格能不能被定拍——可抵达即可（含陷阱格与虚空格）。 */
	public static boolean designatable(int cell) {
		if (Dungeon.level == null) return false;
		if (!Dungeon.level.insideMap(cell)) return false;
		return Dungeon.level.passable[cell] || Dungeon.level.avoid[cell];
	}

	public void setTarget(int cell) {
		this.target = cell;
	}

	public int target() {
		return target;
	}

	public int beats() {
		return beats;
	}

	public void setMode(int mode) {
		this.mode = mode == MODE_POWER ? MODE_POWER : MODE_SHIELD;
	}

	public int mode() {
		return mode;
	}

	public void resetBeats() {
		beats = 0;
	}

	/**
	 * SPSEXPD: 英雄走完一步后调用。返回 true 表示这一拍踩中了且攒满（需要发放奖励）。
	 */
	public boolean onMoved(Hero hero) {
		if (hero == null) return false;
		if (target < 0) return false;
		if (hero.pos == target) {
			beats++;
			target = -1;
			if (beats >= BEATS_REQUIRED) {
				beats = 0;
				return true;
			}
		} else {
			//SPSEXPD: 没踩在拍子上，连击清零
			beats = 0;
			target = -1;
		}
		return false;
	}

	@Override
	public boolean act() {
		spend(TICK);
		return true;
	}

	@Override
	public String iconTextDisplay() {
		return beats + "/" + BEATS_REQUIRED;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BEATS, beats);
		bundle.put(TARGET, target);
		bundle.put(MODE, mode);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		beats = bundle.getInt(BEATS);
		target = bundle.getInt(TARGET);
		mode = bundle.getInt(MODE);
	}
}
