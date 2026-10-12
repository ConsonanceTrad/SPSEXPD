/*
 * 老兵体质 —— 星兵每次升级额外提高生命上限。
 *
 * SPSEXPD: 原为 Hero.levelUp 里 `heroClass == SOLDIER` 的隐藏判定，现由本特质驱动。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class VeteranGrowth extends Perk {

	/** 每级额外提高的生命上限 */
	public static final int HT_PER_LEVEL = 1;

	static {
		InlineText.of(VeteranGrowth.class)
				.t("title", "老兵体质")
				.t("desc", "每次升级时额外提高 1 点生命上限。");
	}

	public VeteranGrowth() {
		super(1, 1);
	}

	//SPSEXPD: 图标待绘制，先用 perks.png 的空白帧占位（不覆写 image()）

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero != null && hero.heroClass == HeroClass.SOLDIER;
	}

	/** 该特质是否生效（星兵职业初始特质）。 */
	public static boolean ownedBy(Hero hero) {
		return hero != null && hero.heroPerk != null && hero.heroPerk.has(VeteranGrowth.class);
	}
}
