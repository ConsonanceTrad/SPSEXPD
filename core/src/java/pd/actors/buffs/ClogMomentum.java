/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.hero.Hero;
import pd.items.unique.items.ClogArmor;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/**
 * SPSEXPD: 木屐的「持续移动」计时。
 *
 * <p>装备木屐时由 {@code Hero.move} 每迈出一步累计；连续移动 {@link #NEEDED} 回合后提高移速，
 * 停下脚步（一回合没移动）就清零；卸下木屐直接失效。</p>
 */
public class ClogMomentum extends Buff {

	/** 连续移动多少回合后开始提速。 */
	public static final int NEEDED = 5;
	/** 提速幅度（+30%）。 */
	public static final float SPEED_BONUS = 0.3f;

	private int steps = 0;
	private int lastPos = -1;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ClogMomentum.class)
			.t("name", "木屐轻步")
			.t("desc", "连续移动 %1$d 回合后，木屐会让你走得更轻快；一旦停下脚步，这份势头就会消失。\n\n已连续移动：%2$d 回合");
	}

	{
		type = buffType.POSITIVE;
	}

	/** 由 Hero.move 在真正迈出一步时调用。 */
	public void step() {
		steps++;
	}

	public boolean active() {
		return steps >= NEEDED;
	}

	public float speedMultiplier() {
		return active() ? 1f + SPEED_BONUS : 1f;
	}

	@Override
	public boolean act() {
		Hero hero = target instanceof Hero ? (Hero) target : null;
		if (hero == null) {
			detach();
			return false;
		}
		//卸下木屐就失效
		if (!(hero.belongings.secondArmor() instanceof ClogArmor)) {
			detach();
			return false;
		}
		//本回合没移动 → 势头清零
		if (hero.pos == lastPos) {
			steps = 0;
		}
		lastPos = hero.pos;
		spend(TICK);
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.HASTE;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", NEEDED, steps);
	}
}
