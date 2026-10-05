/*
 * 无畏 —— 低生命值时不再受恐惧影响。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.messages.InlineText;

public class Fearless extends Perk {

	static {
		InlineText.of(Fearless.class)
				.t("title", "无畏")
				.t("desc", "生命值较低时不会受到恐惧效果的影响。");
	}

	public Fearless() {
		super(1);
	}

	@Override
	public int image() {
		return PerkImageSheet.FEARLESS;
	}

	@Override
	protected boolean canBeGain(Hero hero) {
		return hero.heroClass != HeroClass.WARRIOR || !hero.heroPerk.has(Fearless.class);
	}

	public boolean immune(Hero hero) {
		return true;
	}
}
