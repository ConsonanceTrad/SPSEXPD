package pd.actors.mobs.pets;

import pd.sprites.VioletDragonSprite;
import pd.messages.InlineText;

public class VioletDragon extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(VioletDragon.class)
			.t("name", "紫龙")
			.t("desc", "紫龙属于地龙的一种，它释放恶臭毒素。");
	}



	{ spriteClass = VioletDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.VIOLET_DRAGON; }
}
