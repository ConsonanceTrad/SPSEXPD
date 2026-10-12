/*
 * 讨价还价 —— 信徒购物更便宜。
 *
 * SPSEXPD: 原为 AdventureGuide/Shopkeeper/WndLifeTradeItem 里 `heroClass == FOLLOWER`
 * 的隐藏判定，现由本特质驱动（数值与原先完全一致）。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class Bargain extends Perk {

	/** 向导商店（旅途中遇到的向导）的买价乘数 */
	public static final float GUIDE_BUY_MULT = 0.9f;
	/** 普通商店的买价：单价乘数与章节上限（其他职业为 5 / 25） */
	public static final int SHOP_UNIT_MULT = 4;
	public static final int SHOP_PRICE_CAP = 20;
	/** 用永久生命购买物品的价格（其他职业为 10） */
	public static final int LIFE_TRADE_PRICE = 8;

	static {
		InlineText.of(Bargain.class)
				.t("title", "讨价还价")
				.t("desc", "商店抬价时你总能少付一些：向导的售价降低 10%，"
						+ "商人的售价也更低；用永久生命购买物品时只需 8 点。");
	}

	public Bargain() {
		super(1, 1);
	}

	//SPSEXPD: 图标待绘制，先用 perks.png 的空白帧占位（不覆写 image()）

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero != null && hero.heroClass == HeroClass.FOLLOWER;
	}

	/** 该特质是否生效（信徒职业初始特质）。 */
	public static boolean ownedBy(Hero hero) {
		return hero != null && hero.heroPerk != null && hero.heroPerk.has(Bargain.class);
	}
}
