package pd.actors.mobs.pets;

import pd.sprites.BlueDragonSprite;
import pd.messages.InlineText;

public class BlueDragon extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BlueDragon.class)
			.t("name", "蓝龙")
			.t("desc", "蓝龙精通冰寒之力，也是原先性价比最高的龙。");
	}



	{ spriteClass = BlueDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.BLUE_DRAGON; }
}
