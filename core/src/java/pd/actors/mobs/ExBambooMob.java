/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.sprites.ExBambooSprite;
import pd.messages.InlineText;

/** The legacy evolved bamboo guard. The complete behavior lives in the save-compatible guard base. */
public class ExBambooMob extends SpsExitMobs.GuardBamboo {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ExBambooMob.class)
			.t("name", "竹子精")
			.t("desc", "竹子的进化体，向四周吸取其他生命能量。");
	}



	{ spriteClass = ExBambooSprite.class; }
}
