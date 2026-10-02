package pd.actors.mobs.pets;

import pd.sprites.GoldDragonSprite;
import pd.messages.InlineText;

public class GoldDragon extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GoldDragon.class)
			.t("name", "黄金龙")
			.t("desc", "图鉴里面并没有这条龙的信息。");
	}

	{ spriteClass = GoldDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.GOLD_DRAGON; }
}
