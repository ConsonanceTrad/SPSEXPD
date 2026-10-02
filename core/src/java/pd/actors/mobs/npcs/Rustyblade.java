/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Rustyblade extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Rustyblade.class)
			.t("desc", "混合地牢的翻译者之一，同时也是破碎地牢翻译的协助者。")
			.t("name", "战斗大师-无聊")
			.t("yell1", "嗨，我是无聊，为什么我在这儿。")
			.t("yell2", "攻击提升，攻击下降，防御提升，防御下降，你见过这些buff吗?");
	}



	public Rustyblade() {
		configure(Spec.RUSTYBLADE);
		spriteClass = pd.sprites.RustybladeSprite.class;
	}
}
