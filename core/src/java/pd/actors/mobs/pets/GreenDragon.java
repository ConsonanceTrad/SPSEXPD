package pd.actors.mobs.pets;

import pd.sprites.GreenDragonSprite;
import pd.messages.InlineText;

public class GreenDragon extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GreenDragon.class)
			.t("name", "绿龙")
			.t("desc", "绿龙一般精通闪电的力量。但由于原来没有那么多的闪电来源，所以绿龙十分稀缺。");
	}



	{ spriteClass = GreenDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.GREEN_DRAGON; }
}
