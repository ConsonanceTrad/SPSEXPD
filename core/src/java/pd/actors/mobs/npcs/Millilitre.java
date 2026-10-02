/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Millilitre extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Millilitre.class)
			.t("name", "millilitre isaac")
			.t("desc", "因为意外来到这里的测试者之一，他被吓得不轻。")
			.t("yell1", "不要管我，让我静静。")
			.t("yell2", "这个世界太可怕了...妈妈...我想回家...");
	}



	public Millilitre() {
		configure(Spec.MILLILITRE);
		spriteClass = pd.sprites.MillilitreSprite.class;
	}
}
