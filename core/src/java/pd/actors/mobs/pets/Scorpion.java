package pd.actors.mobs.pets;

import pd.sprites.ScorpionSprite;
import pd.messages.InlineText;

public class Scorpion extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Scorpion.class)
			.t("name", "血蝎")
			.t("desc", "一个大号嗜血的蝎子。它的尾巴镶嵌着一个危险的蜇刺。");
	}

	{ spriteClass = ScorpionSprite.class; properties.add(Property.BEAST); updateStats(true); }
	@Override protected Kind kind() { return Kind.SCORPION; }
}
