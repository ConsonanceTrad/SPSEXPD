package pd.actors.mobs.pets;

import pd.sprites.LerySprite;
import pd.messages.InlineText;

public class LeryFire extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LeryFire.class)
			.t("name", "修齐元素")
			.t("desc", "早期破碎翻译组的修齐所制作的元素。它十分不稳定。");
	}



	{ spriteClass = LerySprite.class; properties.add(Property.ELEMENT); updateStats(true); }
	@Override protected Kind kind() { return Kind.LERY_FIRE; }
}
