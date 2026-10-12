/*
 * 天佑 —— 星兵天生幸运。
 *
 * SPSEXPD: 原为 LuckyBadge.luckBonus 里 `heroClass == SOLDIER` 的隐藏判定，现由本特质驱动。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class Fortunate extends Perk {

	/** 该特质提供的幸运值 */
	public static final int LUCK = 5;

	static {
		InlineText.of(Fortunate.class)
				.t("title", "天佑")
				.t("desc", "幸运 +5。");
	}

	public Fortunate() {
		super(1, 1);
	}

	//SPSEXPD: 图标待绘制，先用 perks.png 的空白帧占位（不覆写 image()）

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero != null && hero.heroClass == HeroClass.SOLDIER;
	}

	/** 该特质是否生效（星兵职业初始特质）。 */
	public static boolean ownedBy(Hero hero) {
		return hero != null && hero.heroPerk != null && hero.heroPerk.has(Fortunate.class);
	}

	/** 供幸运体系查询该特质提供的幸运值。 */
	public static int luckOf(Hero hero) {
		return ownedBy(hero) ? LUCK : 0;
	}
}
