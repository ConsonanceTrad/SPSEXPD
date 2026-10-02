package pd.actors.mobs.pets;

import pd.sprites.RedDragonSprite;
import pd.messages.InlineText;

public class RedDragon extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(RedDragon.class)
			.t("name", "红龙")
			.t("desc", "红龙一般擅长喷射火焰，所以它们非常常见。");
	}



	{ spriteClass = RedDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.RED_DRAGON; }
}
