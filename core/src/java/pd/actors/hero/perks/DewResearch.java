/*
 * 露珠研究 —— 改良露珠用法：随机降低露珠瓶 / 露珠瓶效果的露珠消耗。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;
import render.utils.math.Random;

public class DewResearch extends Perk {

	static {
		InlineText.of(DewResearch.class)
			.t("title", "露珠研究")
			.t("desc", "你改良了露珠的用法：每次使用露珠瓶 / 露珠瓶的效果时，随机降低 %1$d%%~%2$d%% 的露珠消耗。");
	}

	public DewResearch() {
		super(2);
	}

	/** 折扣下限 */
	public float discountMin() {
		return level() >= 2 ? 0.20f : 0.10f;
	}

	/** 折扣上限 */
	public float discountMax() {
		return level() >= 2 ? 0.40f : 0.30f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc",
				Math.round(discountMin() * 100), Math.round(discountMax() * 100));
	}

	/** 本次消耗的折扣比例（每次使用随机） */
	public float rollDiscount() {
		return discountMin() + Random.Float() * (discountMax() - discountMin());
	}

	/** 应用本次折扣（至少保留 1 点消耗） */
	public int reduce(int cost) {
		if (cost <= 0) return cost;
		return Math.max(1, Math.round(cost * (1f - rollDiscount())));
	}

	/** 折扣上限（最低可能消耗） */
	public int costMin(int base) {
		return Math.max(1, Math.round(base * (1f - discountMax())));
	}

	/** 折扣下限（最高可能消耗） */
	public int costMax(int base) {
		return Math.max(1, Math.round(base * (1f - discountMin())));
	}

	/** 便捷入口：英雄拥有该特质时打折 */
	public static int applyDiscount(Hero hero, int cost) {
		if (hero == null || hero.heroPerk == null) return cost;
		DewResearch research = hero.heroPerk.get(DewResearch.class);
		return research == null ? cost : research.reduce(cost);
	}
}