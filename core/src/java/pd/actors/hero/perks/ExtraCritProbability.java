/*
 * 额外暴击率 —— 直接提高英雄暴击率（数值型特质）。
 */

package pd.actors.hero.perks;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class ExtraCritProbability extends Perk.Additional {

	static {
		InlineText.of(ExtraCritProbability.class)
				.t("title", "精准打击")
				.t("desc", "提供 %s%% 的额外暴击几率。");
	}

	public ExtraCritProbability() {
		super(5);
		addTags(Tag.Melee, Tag.Crit);
	}

	@Override
	public int image() {
		return PerkImageSheet.CRIT_PROB;
	}

	public float extraProb() {
		return level() * 0.05f + 0.01f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(Math.round(extraProb() * 100)));
	}

	@Override
	public void onGain() {
		Hero hero = Dungeon.hero;
		if (hero != null) hero.criticalChance += extraProb();
	}

	@Override
	public void onLose() {
		Hero hero = Dungeon.hero;
		if (hero != null) hero.criticalChance = Math.max(0f, hero.criticalChance - extraProb());
	}
}
