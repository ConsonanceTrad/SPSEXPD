/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 定时型特质 —— 移植自 Darkest Pixel Dungeon 0.7.2 的 TimingPerk。
 * 获得特质时挂一个周期触发的 Buff，失去时卸下。
 */

package pd.actors.hero.perks;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;

public abstract class TimingPerk extends Perk {

	private final Class<? extends Timing> timing;

	public TimingPerk(Class<? extends Timing> timing) {
		this(timing, 1, 1);
	}

	public TimingPerk(Class<? extends Timing> timing, int maxLevel, int level) {
		super(maxLevel, level);
		this.timing = timing;
	}

	/** 每次触发时执行的效果 */
	public abstract void trigger(Hero hero);

	@Override
	public void onGain() {
		if (Dungeon.hero != null) Buff.affect(Dungeon.hero, timing);
	}

	@Override
	public void upgrade() {
		if (Dungeon.hero != null) {
			Timing t = Dungeon.hero.buff(timing);
			if (t != null) t.upgrade();
		}
		super.upgrade();
	}

	@Override
	public void onLose() {
		if (Dungeon.hero != null) Buff.detach(Dungeon.hero, timing);
	}

	public static abstract class Timing extends Buff {

		private float time;

		public Timing() {
			this(10f);
		}

		public Timing(float time) {
			this.time = time;
		}

		public abstract void trigger();

		public void upgrade() {
		}

		protected float time() {
			return time;
		}

		protected void time(float t) {
			this.time = t;
		}

		@Override
		public boolean act() {
			trigger();
			spend(time);
			return true;
		}
	}
}
