/*
 * 卸甲潜行 —— 无甲时更难被察觉。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class BaredStealth extends Perk {

	static {
		InlineText.of(BaredStealth.class)
				.t("title", "卸甲潜行")
				.t("desc", "卸去铠甲之后，你的行动变得更加难以察觉。");
	}

	public BaredStealth() {
		super(1);
		addTags(Tag.Bare, Tag.Evade, Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.BARED_STEALTH;
	}

	@Override
	protected boolean canBeGain(Hero hero) {
		return Bare.isBare(hero) || !hero.heroPerk.has(BaredStealth.class);
	}

	public float extraStealth() {
		return 0.5f;
	}
}
