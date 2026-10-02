/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.sprites.BombBugSprite;
import pd.messages.InlineText;

/** Original SPS runtime/save identity for the fully migrated stone bug. */
public class BombBug extends SpsExitMobs.GuardBombBug {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BombBug.class)
			.t("name", "霜石虫")
			.t("desc", "包裹着冰石的虫子。");
	}

	{
		spriteClass = BombBugSprite.class;
		properties.remove(Property.ICY);
		properties.add(Property.BEAST);
	}
}
