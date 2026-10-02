/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class CatSheep extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(CatSheep.class)
			.t("name", "彩虹猫")
			.t("yell1", "喵")
			.t("yell2", "喵")
			.t("desc", "彩虹猫");
	}

	public CatSheep() {
		configure(Spec.CAT_SHEEP);
		spriteClass = pd.sprites.CatSheepSprite.class;
	}
}
