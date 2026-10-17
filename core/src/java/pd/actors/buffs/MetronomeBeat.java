/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.mechanics.pathfind.PathFinder;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/**
 * SPSEXPD: 节拍器——「踩拍」计数。
 *
 * <p>节拍不靠玩家指定：英雄_每行动一次_，系统就在他周围可抵达的格里随机标出下一拍
 * （陷阱格、虚空格也算，踩上去只是有后果）。下一步正好走到那一格就算踩中，连击 +1，否则清零。
 *
 * <p>连击满 {@link #BEATS_REQUIRED} 后不再立刻发奖，而是进入「等待结算」状态：
 * <b>接下来那一次行动的类型</b>决定奖励——移动给护盾，攻击（或其它非移动动作）给强化攻击。</p>
 */
public class MetronomeBeat extends Buff {

	private static final String BEATS = "beats";
	private static final String BEAT_CELL = "beatCell";
	private static final String AWAITING = "awaiting";

	/** 触发所需的连击数。 */
	public static final int BEATS_REQUIRED = 4;

	/** 奖励：护盾。 */
	public static final int MODE_SHIELD = 0;
	/** 奖励：强化攻击。 */
	public static final int MODE_POWER = 1;

	private int beats = 0;
	/** 系统标出的下一拍格（-1 = 还没标）。 */
	private int beatCell = -1;
	/** 连击已满、正等待「下一次行动」决定奖励类型。 */
	private boolean awaiting = false;

	{
		type = buffType.POSITIVE;
	}

	/** SPSEXPD: 这一格能不能被标成拍子——可抵达即可（含陷阱格与虚空格）。 */
	public static boolean designatable(int cell) {
		if (Dungeon.level == null) return false;
		if (!Dungeon.level.insideMap(cell)) return false;
		return Dungeon.level.passable[cell] || Dungeon.level.avoid[cell];
	}

	public int beats() {
		return beats;
	}

	public int beatCell() {
		return beatCell;
	}

	public boolean awaiting() {
		return awaiting;
	}

	/** SPSEXPD: 在英雄周围可抵达的八格里随机定下一拍。 */
	public void autoBeat(Hero hero) {
		if (hero == null || Dungeon.level == null) {
			beatCell = -1;
			return;
		}
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = hero.pos + offset;
			if (designatable(cell)) candidates.add(cell);
		}
		beatCell = candidates.isEmpty() ? -1 : candidates.get(Random.Int(candidates.size()));
	}

	/**
	 * SPSEXPD: 英雄一次行动结束后调用——先结算上一拍是否踩中，再自动定下一拍。
	 * 连击攒满时进入 awaiting，等下一次行动决定奖励。
	 */
	public void afterAction(Hero hero) {
		if (hero == null) return;
		if (beatCell >= 0 && hero.pos == beatCell) {
			beats++;
			if (beats >= BEATS_REQUIRED) {
				beats = 0;
				awaiting = true;
			}
		} else {
			beats = 0;
		}
		if (!awaiting) autoBeat(hero);
	}

	/** SPSEXPD: 取走这次待结算的奖励类型（并把状态清干净）。 */
	public int consumeAwaiting(int movedReward, int otherReward) {
		int mode = awaiting ? movedReward : otherReward;
		awaiting = false;
		return mode;
	}

	/** SPSEXPD: 进入 waiting 之前不再自动定拍，发奖后由调用方重新 autoBeat。 */
	public void finishReward(Hero hero) {
		awaiting = false;
		beats = 0;
		autoBeat(hero);
	}

	@Override
	public boolean act() {
		//SPSEXPD: 节拍只在英雄行动后推进（见 afterAction），buff 自己不做任何事
		spend(TICK);
		return true;
	}

	@Override
	public String iconTextDisplay() {
		return awaiting ? "!" : beats + "/" + BEATS_REQUIRED;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BEATS, beats);
		bundle.put(BEAT_CELL, beatCell);
		bundle.put(AWAITING, awaiting);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		beats = bundle.getInt(BEATS);
		beatCell = bundle.getInt(BEAT_CELL);
		awaiting = bundle.getBoolean(AWAITING);
	}
}
