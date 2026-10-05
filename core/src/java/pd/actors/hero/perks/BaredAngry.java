/*
 * 暴露狂 —— 无甲时提高攻击力与攻击速度。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class BaredAngry extends Perk {

	static {
		InlineText.of(BaredAngry.class)
				.t("title", "暴露狂")
				.t("desc", "未装备护甲时，你的攻击力和攻击速度都会得到较大提升。");
	}

	public BaredAngry() {
		super(1);
		addTags(Tag.Bare, Tag.Crit);
	}

	@Override
	public int image() {
		return PerkImageSheet.BARED_ANGRY;
	}

	@Override
	protected boolean canBeGain(Hero hero) {
		return Bare.isBare(hero) || !hero.heroPerk.has(BaredAngry.class);
	}

	public float damageMultiplier() {
		return 1.25f;
	}

	public float speedMultiplier() {
		return 0.8f;
	}
}
