/*
 * 灵巧成长 —— 升级时获得更多防御技能。
 */

package pd.actors.hero.perks;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class ExtraDexterousGrowth extends Perk {

	static {
		InlineText.of(ExtraDexterousGrowth.class)
				.t("title", "灵巧成长")
				.t("desc", "每次升级额外获得 %d 点防御技能。");
	}

	public ExtraDexterousGrowth() {
		super(5);
		addTags(Tag.Evade);
	}

	@Override
	public int image() {
		return PerkImageSheet.DEX_GROWTH;
	}

	public int extraDef() {
		return Math.round(level() * 0.5f);
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", extraDef());
	}

	public void onHeroUpgrade(Hero hero) {
		hero.improveDefenseSkill(Math.max(1, extraDef()));
	}

	public static void apply(Hero hero) {
		if (hero == null || hero.heroPerk == null) return;
		ExtraDexterousGrowth g = hero.heroPerk.get(ExtraDexterousGrowth.class);
		if (g != null) g.onHeroUpgrade(hero);
	}
}
