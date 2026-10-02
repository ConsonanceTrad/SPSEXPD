package pd.actors.mobs.pets;

import pd.sprites.BlueGirlSprite;
import pd.messages.InlineText;

public class BlueGirl extends PET {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BlueGirl.class)
			.t("name", "蓝色人偶")
			.t("desc", "来自Ren像素地牢里的玩具，身着护甲，手戴拳套，看上去很擅长战斗。");
	}

	{ spriteClass = BlueGirlSprite.class; properties.add(Property.ELF); updateStats(true); }
	@Override protected Kind kind() { return Kind.BLUE_GIRL; }
}
