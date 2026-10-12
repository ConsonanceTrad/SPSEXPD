/*
 * 返场 —— 演员每次升级时获得少量护盾。
 *
 * SPSEXPD: 原为 Hero.levelUp 里 `heroClass == PERFORMER` 的隐藏判定，现由本特质驱动。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class Encore extends Perk {

	static {
		InlineText.of(Encore.class)
				.t("title", "返场")
				.t("desc", "每次升级时获得 3 + 等级/5 点护盾。");
	}

	public Encore() {
		super(1, 1);
	}

	//SPSEXPD: 图标待绘制，先用 perks.png 的空白帧占位（不覆写 image()）

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero != null && hero.heroClass == HeroClass.PERFORMER;
	}

	/** 该特质是否生效（演员职业初始特质）。 */
	public static boolean ownedBy(Hero hero) {
		return hero != null && hero.heroPerk != null && hero.heroPerk.has(Encore.class);
	}

	/** 升到 lvl 级时获得的护盾量 */
	public static int shieldOnLevelUp(int lvl) {
		return 3 + lvl / 5;
	}
}
