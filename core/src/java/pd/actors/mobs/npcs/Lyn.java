/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Lyn extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Lyn.class)
			.t("desc", "破碎地牢的翻译者之一，是个脚男。")
			.t("name", "受咒ankh")
			.t("yell1", "燃烧!净化!为了萨格拉斯的伟大远征!")
			.t("yell2", "来杯魔能饮料吗?哦，你想知道宠物吃什么吗?想想它们是什么，是常规生物还是奇幻事物，是素食还是肉食。当然，没有宠物可以拒绝口粮，那是特制的。");
	}

	public Lyn() {
		configure(Spec.LYN);
		spriteClass = pd.sprites.LynSprite.class;
	}
}
