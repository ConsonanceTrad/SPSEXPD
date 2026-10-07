/*
 * 讨价还价 —— 商店购买价格降低。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class Discount extends Perk {

	static {
		InlineText.of(Discount.class)
				.t("title", "讨价还价")
				.t("desc", "商店售价降低 %s%%。%s");
	}

	public Discount() {
		this(1);
	}

	public Discount(int level) {
		super(2, level);
	}

	@Override
	public int image() {
		return level() >= 1 ? PerkImageSheet.DISCOUNT : PerkImageSheet.DISCOUNT_NEG;
	}

	/** 价格乘数；负等级表示反向加价 */
	public float priceMultiplier() {
		return 1f - 0.15f * level();
	}

	@Override
	public String description() {
		String extra = level() < 0 ? "\n（负等级会反向提高价格）" : "";
		return pd.messages.Messages.get(this, "desc", num(Math.abs(Math.round((priceMultiplier() - 1f) * 100))), extra);
	}
}
