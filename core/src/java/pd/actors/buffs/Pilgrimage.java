/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 禅杖 / 九环锡杖装备期间的状态——「赶路」。
 *
 * <p>_连续移动_的时间越久加成就越高（停下即清零），此外_周围没有怪物_时另有加成。</p>
 */
public class Pilgrimage extends Buff {

	private static final String MOVES = "moves";
	private static final String LAST = "last";
	private static final String REROLL = "reroll";

	/** 连续移动的回合数。 */
	private int moves = 0;
	private int lastPos = -1;
	/** 九环锡杖：加速状态下的首次攻击是否还能重随。 */
	private boolean rerollReady = true;

	/** 每连续移动这么多回合，提速一档。 */
	public static final int MOVES_PER_STEP = 3;
	/** 每档提速。 */
	public static final float STEP_BONUS = 0.075f;
	/** 连续移动的提速上限。 */
	public static final float MAX_MOVE_BONUS = 0.30f;
	/** 周围没有怪物时的提速。 */
	public static final float CLEAR_BONUS = 0.15f;
	/** 「周围」的判定半径。 */
	public static final int CLEAR_RADIUS = 4;

	{
		type = buffType.POSITIVE;
	}

	public float speedMultiplier() {
		float mult = 1f;
		if (moves > 0) {
			mult += Math.min(MAX_MOVE_BONUS, STEP_BONUS * (moves / MOVES_PER_STEP));
		}
		if (noMonstersNearby()) {
			mult += CLEAR_BONUS;
		}
		return mult;
	}

	public boolean speedActive() {
		return speedMultiplier() > 1f;
	}

	/** SPSEXPD: 取用一次「加速状态下的首次攻击重随」。 */
	public boolean consumeReroll() {
		if (!speedActive() || !rerollReady) return false;
		rerollReady = false;
		return true;
	}

	private boolean noMonstersNearby() {
		Hero hero = Dungeon.hero;
		if (hero == null || Dungeon.level == null) return true;
		for (Mob m : Dungeon.level.mobs()) {
			if (m == null || m.alignment != Char.Alignment.ENEMY) continue;
			if (Dungeon.level.distance(hero.pos, m.pos) <= CLEAR_RADIUS) return false;
		}
		return true;
	}

	@Override
	public boolean act() {
		if (target instanceof Hero) {
			Hero hero = (Hero) target;
			//SPSEXPD: 不再拿着禅杖/九环锡杖时自动结束
			if (!(hero.belongings.weapon() instanceof pd.items.unique.items.PilgrimStaff)) {
				detach();
				return true;
			}
			if (hero.pos != lastPos) {
				moves++;
				lastPos = hero.pos;
			} else {
				//SPSEXPD: 停下即清零连续移动
				moves = 0;
			}
			if (!speedActive()) rerollReady = true;
		}
		spend(TICK);
		return true;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(MOVES, moves);
		bundle.put(LAST, lastPos);
		bundle.put(REROLL, rerollReady);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		moves = bundle.getInt(MOVES);
		lastPos = bundle.getInt(LAST);
		rerollReady = bundle.getBoolean(REROLL);
	}
}
