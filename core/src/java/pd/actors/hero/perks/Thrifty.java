/*
 * 精打细算 —— 信徒消耗药水/药丸时有一定概率不消耗。
 *
 * SPSEXPD: 原为 Brewed/Pill 里 `heroClass == FOLLOWER` 的隐藏判定，现由本特质驱动。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class Thrifty extends Perk {

	/** 不消耗的掷骰分母：Random.Int(N) == 0 时不消耗（即 1/N 概率保留物品） */
	public static final int KEEP_CHANCE_ROLLS = 10;

	static {
		InlineText.of(Thrifty.class)
				.t("title", "精打细算")
				.t("desc", "喝下的药水与吃下的药丸有 1/10 的概率不被消耗。");
	}

	public Thrifty() {
		super(1, 1);
	}

	//SPSEXPD: 图标待绘制，先用 perks.png 的空白帧占位（不覆写 image()）

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero != null && hero.heroClass == HeroClass.FOLLOWER;
	}

	/** 该特质是否生效（信徒职业初始特质）。 */
	public static boolean ownedBy(Hero hero) {
		return hero != null && hero.heroPerk != null && hero.heroPerk.has(Thrifty.class);
	}
}
