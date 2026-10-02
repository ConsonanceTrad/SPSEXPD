package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumScrollAmuletScrollDict;
public class SkillOfMig extends SkillBook {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SkillOfMig.class)
			.t("name", "智力之书")
			.t("ac_read", "阅读")
			.t("desc", "看上去是一张普通的羊皮卷，但上面记载了高超的施法技巧。")
			.t("skillup", "你感觉你的魔法能力提升了。");
	}



	{ image = ConsumScrollAmuletScrollDict.SCROLL_OF_MAGIC; }
	@Override void apply(Hero hero) { hero.improveMagicSkill(1); }
	@Override public int value() { return 200 * quantity; }
}
