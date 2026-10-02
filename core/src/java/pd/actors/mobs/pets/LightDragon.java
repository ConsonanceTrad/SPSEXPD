package pd.actors.mobs.pets;

import pd.sprites.LightDragonSprite;
import pd.messages.InlineText;

public class LightDragon extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LightDragon.class)
			.t("name", "光龙")
			.t("desc", "光龙，顾名思义，是发光的龙。");
	}



	{ spriteClass = LightDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.LIGHT_DRAGON; }
}
