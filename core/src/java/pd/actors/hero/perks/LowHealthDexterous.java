/*
 * 濒死灵敏 —— 低生命值时提高闪避。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class LowHealthDexterous extends Perk {

	static {
		InlineText.of(LowHealthDexterous.class)
				.t("title", "濒死灵敏")
				.t("desc", "生命值低于 30%% 时获得额外闪避几率。");
	}

	public LowHealthDexterous() {
		super(3);
		addTags(Tag.Evade, Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.LOW_HEALTH_DEX;
	}

	public float extraEvasion(Hero hero) {
		if (hero.HP > hero.HT * 0.3f) return 0f;
		return (float) Math.pow(1.5f, level()) * 0.2f;
	}
}
