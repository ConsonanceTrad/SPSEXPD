package pd.actors.mobs.pets;

import pd.sprites.ShadowDragonSprite;
import pd.messages.InlineText;

public class ShadowDragon extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ShadowDragon.class)
			.t("name", "暗影龙")
			.t("desc", "暗影龙属于黑暗，但需要光明才能将它召唤。");
	}



	{ spriteClass = ShadowDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.SHADOW_DRAGON; }
}
