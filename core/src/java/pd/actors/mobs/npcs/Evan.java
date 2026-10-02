/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Evan extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Evan.class)
			.t("name", "Evan")
			.t("desc", "破碎地牢的制作者。说实在的我无法确定这代表evan还是破碎...他们的图标是一样的。")
			.t("yell1", "欢迎，冒险者。这个地牢可不太平呢。-来自00-Evan")
			.t("yell2", "我比较低调，所以当我听到hmdzl001和我说要我考虑台词的时候，其实我是拒绝的。")
			.t("yell3", "我需要尽我全力来使破碎地牢更加好玩，更加有趣，更加合理，更加有挑战性。");
	}



	public Evan() {
		configure(Spec.EVAN);
		spriteClass = pd.sprites.EvanSprite.class;
	}
}
