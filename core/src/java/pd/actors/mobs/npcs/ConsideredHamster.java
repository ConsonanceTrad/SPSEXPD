/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class ConsideredHamster extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ConsideredHamster.class)
			.t("name", "ConsideredHamster的宝箱怪")
			.t("desc", "YAPD的作者的宠物。恕我直言，打不通YAPD简单难度的都是垃圾。")
			.t("yell1", "嘿，我现在很饿，能给我喂1美刀吗?")
			.t("yell2", "Pineapples!");
	}

	public ConsideredHamster() {
		configure(Spec.CONSIDERED_HAMSTER);
		spriteClass = pd.sprites.MimicSprite.class;
	}
}
