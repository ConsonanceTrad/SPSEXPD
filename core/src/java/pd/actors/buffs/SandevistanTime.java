/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.hero.Hero;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 斯安威斯坦开启后的「子弹时间」。
 *
 * <p>期间_只有英雄推进_——其它 Actor（敌人、陷阱计时等）全部停在原地；
 * 英雄的攻击_照常即时结算_（不延迟），时间一到就恢复正常推进。</p>
 */
public class SandevistanTime extends Buff {

	private static final String TURNS = "turns";

	private int turns = 0;

	{
		type = buffType.POSITIVE;
	}

	public SandevistanTime setup(int turns) {
		this.turns += Math.max(0, turns);
		return this;
	}

	public int turnsLeft() {
		return turns;
	}

	/** SPSEXPD: 当前是否处于子弹时间（供 Actor.process 判断）。 */
	public static boolean heroOnly() {
		Hero hero = Dungeon.hero;
		return hero != null && hero.buff(SandevistanTime.class) != null;
	}

	/** SPSEXPD: 由 Hero.act() 驱动计时（buff 自己也是 Actor，会被冻结跳过）。 */
	public void tick() {
		turns--;
		if (turns <= 0) {
			detach();
		}
	}

	@Override
	public boolean act() {
		//SPSEXPD: 计时交给英雄，这里什么也不做
		spend(TICK);
		return true;
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString(Math.max(0, turns));
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TURNS, turns);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		turns = bundle.getInt(TURNS);
	}
}
