/*
 * 灵巧 —— 直接提高防御技能（数值型特质）。
 */

package pd.actors.hero.perks;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class ExtraDexterous extends Perk.Additional {

	static {
		InlineText.of(ExtraDexterous.class)
				.t("title", "灵巧")
				.t("desc", "额外提升 %s 点防御技能。");
	}

	public ExtraDexterous() {
		super(5);
		addTags(Tag.Evade);
	}

	@Override
	public int image() {
		return PerkImageSheet.DEX_EXTRA;
	}

	public int extraDef() {
		return level() * 3;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", num(extraDef()));
	}

	@Override
	public void onGain() {
		Hero hero = Dungeon.hero;
		if (hero != null) hero.improveDefenseSkill(extraDef());
	}

	@Override
	public void onLose() {
		Hero hero = Dungeon.hero;
		if (hero != null) hero.improveDefenseSkill(-extraDef());
	}
}
