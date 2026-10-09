/*
 * 幸运儿 —— 幸运 +1。
 *
 * 由「流浪者拾荒心得分享」礼物解锁，开局直接获得（取代原本发一枚可升级的幸运胸章）。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class BornLucky extends Perk {

	/** 该特质提供的幸运值。 */
	public static final int LUCK = 1;

	static {
		InlineText.of(BornLucky.class)
				.t("title", "幸运儿")
				.t("desc", "你的运气比常人好一些：幸运 +1。");
	}

	public BornLucky() {
		super(1, 1);
	}

	@Override
	public int image() {
		return PerkImageSheet.BORN_LUCKY;
	}

	/** 供幸运体系查询该特质提供的幸运值。 */
	public static int luckOf(Hero hero) {
		if (hero == null || hero.heroPerk == null) return 0;
		return hero.heroPerk.has(BornLucky.class) ? LUCK : 0;
	}
}
