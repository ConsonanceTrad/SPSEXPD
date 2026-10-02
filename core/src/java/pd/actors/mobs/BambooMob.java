/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.sprites.MobBambooSprite;
import pd.messages.InlineText;

/** Original SPS runtime/save identity for the prison bamboo. */
public class BambooMob extends SpsPrisonMobs.BambooMob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BambooMob.class)
			.t("name", "竹子")
			.t("desc", "监狱长养的奇怪植物。");
	}



	{ spriteClass = MobBambooSprite.class; }
}
