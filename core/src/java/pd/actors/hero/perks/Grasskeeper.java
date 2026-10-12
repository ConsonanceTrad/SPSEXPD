/*
 * 踏草无声 —— 女猎手穿过草丛时不将其践踏。
 *
 * SPSEXPD: 原为 HighGrass/Hero 里 `heroClass == HUNTRESS` 的隐藏判定，现由本特质驱动。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class Grasskeeper extends Perk {

	static {
		InlineText.of(Grasskeeper.class)
				.t("title", "踏草无声")
				.t("desc", "你走过高草时不会将其践踏成普通草地，垄沟草也不会被踩坏；"
						+ "站在垄沟草上时，也无法再原地把它踏平。");
	}

	public Grasskeeper() {
		super(1, 1);
	}

	//SPSEXPD: 图标待绘制，先用 perks.png 的空白帧占位（不覆写 image()）

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero != null && hero.heroClass == HeroClass.HUNTRESS;
	}

	/** 该特质是否生效（女猎手职业初始特质）。 */
	public static boolean ownedBy(Hero hero) {
		return hero != null && hero.heroPerk != null && hero.heroPerk.has(Grasskeeper.class);
	}
}
