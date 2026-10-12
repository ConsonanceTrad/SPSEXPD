/*
 * 舞台节奏 —— 演员获得经验与击杀敌人时进入节奏状态。
 *
 * SPSEXPD: 原为 Hero.gainExp 与 Mob.die 里 `heroClass == PERFORMER` 的隐藏判定，现由本特质驱动。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.messages.InlineText;

public class StageRhythm extends Perk {

	/** 获得经验时的节奏时长（秒）；巨星更长 */
	public static final float EXP_DURATION = 3f;
	public static final float EXP_DURATION_SUPERSTAR = 5f;
	/** 击杀敌人时的节奏时长（秒） */
	public static final float KILL_DURATION = 10f;
	/** 击杀时若背包中有铁锹，为其充能的点数 */
	public static final int SHOVEL_CHARGE = 5;

	static {
		InlineText.of(StageRhythm.class)
				.t("title", "舞台节奏")
				.t("desc", "每次获得经验都会进入 3 秒的节奏（巨星为 5 秒）；击杀敌人时节奏延长 10 秒，"
						+ "并让背包中的铁锹充能 5 点。");
	}

	public StageRhythm() {
		super(1, 1);
	}

	//SPSEXPD: 图标待绘制，先用 perks.png 的空白帧占位（不覆写 image()）

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero != null && hero.heroClass == HeroClass.PERFORMER;
	}

	/** 该特质是否生效（演员职业初始特质）。 */
	public static boolean ownedBy(Hero hero) {
		return hero != null && hero.heroPerk != null && hero.heroPerk.has(StageRhythm.class);
	}

	/** 获得经验时的节奏时长 */
	public static float expDuration(Hero hero) {
		return hero != null && hero.subClass == HeroSubClass.SUPERSTAR
				? EXP_DURATION_SUPERSTAR : EXP_DURATION;
	}
}
