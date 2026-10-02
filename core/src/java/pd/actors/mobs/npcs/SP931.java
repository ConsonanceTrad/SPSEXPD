/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class SP931 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SP931.class)
			.t("name", "被放逐者-931451545")
			.t("desc", "破碎地牢的翻译者之一。虽然我对他了解不多，但是目前他人对他的评价呈两级分化状态。")
			.t("yell1", "嘿，还在看空洞无聊的文本贴图吗，来试试我的蓝猫地牢吧。")
			.t("yell2", "人生如果不装B的话还有什么意思呢?");
	}



	public SP931() {
		configure(Spec.SP931);
		spriteClass = pd.sprites.SP931Sprite.class;
	}
}
