/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.sprites.GreatMossSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the great moss. */
public class Greatmoss extends SpsCityMobs.GreatMoss {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Greatmoss.class)
			.t("name", "孢子巨人")
			.t("desc", "植物与元素生命的巨大混合物，受击时会释放危险孢子。");
	}


	{
		spriteClass = GreatMossSprite.class;
	}
}
