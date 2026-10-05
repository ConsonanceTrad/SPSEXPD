/*
 * 卸甲疾行 —— 无甲时提高移动速度与闪避。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class BaredSwiftness extends Perk {

	static {
		InlineText.of(BaredSwiftness.class)
				.t("title", "卸甲疾行")
				.t("desc", "未装备护甲时，你的移动速度和闪避都会得到较大提升。");
	}

	public BaredSwiftness() {
		super(1);
		addTags(Tag.Bare, Tag.Evade);
	}

	@Override
	public int image() {
		return PerkImageSheet.BARED_SWIFTNESS;
	}

	@Override
	protected boolean canBeGain(Hero hero) {
		return Bare.isBare(hero) || !hero.heroPerk.has(BaredSwiftness.class);
	}

	public float speedMultiplier() {
		return 0.8f;
	}

	public float extraEvasion() {
		return 0.15f;
	}
}
