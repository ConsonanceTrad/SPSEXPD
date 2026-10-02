/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class HeXA extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HeXA.class)
			.t("name", "UNIST HeXA")
			.t("desc", "UNIST地牢的作者。UNIST地牢还行，只是只有看韩文你才能理解里面的梗。")
			.t("yell1", "我是UNIST地牢的制作者。我觉得你应该没听说过这个mod。")
			.t("yell2", "目前我只在韩国发布了这个地牢mod。");
	}

	public HeXA() {
		configure(Spec.HEXA);
		spriteClass = pd.sprites.HeXASprite.class;
	}
}
