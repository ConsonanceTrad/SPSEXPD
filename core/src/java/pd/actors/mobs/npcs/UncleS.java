/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class UncleS extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(UncleS.class)
			.t("name", "斯堡罗提德大叔")
			.t("yell1", "小子，想和我锻炼一下吗")
			.t("desc", "工会里的力士，是个猛男。");
	}

	public UncleS() {
		configure(Spec.UNCLE_S);
		spriteClass = pd.sprites.UncleSSprite.class;
	}
}
