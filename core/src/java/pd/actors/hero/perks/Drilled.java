/*
 * 操练 —— 星兵的命中与闪避略微提高。
 *
 * SPSEXPD: 原为 Hero.attackSkill/defenseSkill 里 `heroClass == SOLDIER` 的隐藏判定，现由本特质驱动。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class Drilled extends Perk {

	/** 命中与闪避的乘数 */
	public static final float SKILL_MULT = 1.05f;

	static {
		InlineText.of(Drilled.class)
				.t("title", "操练")
				.t("desc", "你的命中与闪避各提高 5%。");
	}

	public Drilled() {
		super(1, 1);
	}

	//SPSEXPD: 图标待绘制，先用 perks.png 的空白帧占位（不覆写 image()）

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero != null && hero.heroClass == HeroClass.SOLDIER;
	}

	/** 该特质是否生效（星兵职业初始特质）。 */
	public static boolean ownedBy(Hero hero) {
		return hero != null && hero.heroPerk != null && hero.heroPerk.has(Drilled.class);
	}
}
