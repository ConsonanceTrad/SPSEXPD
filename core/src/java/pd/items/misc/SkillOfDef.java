package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
public class SkillOfDef extends SkillBook {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SkillOfDef.class)
			.t("name", "敏捷之书")
			.t("ac_read", "阅读")
			.t("desc", "看上去是一张普通的羊皮卷，但上面记载了高超的闪避技巧。")
			.t("skillup", "你感觉你的闪避能力提升了。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override void apply(Hero hero) { hero.improveDefenseSkill(1); }
	@Override public int value() { return 100 * quantity; }
}
