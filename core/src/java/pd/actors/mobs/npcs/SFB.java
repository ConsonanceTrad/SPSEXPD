/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class SFB extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SFB.class)
			.t("name", "流浪法师-破碎暴风火杖")
			.t("desc", "破碎地牢的翻译者之一。")
			.t("yell1", "打牌吗，跑团吗，带我一个带我一个。")
			.t("yell2", "火属性是最具有破坏力的属性。它的输出也是最高的")
			.t("hello", "看我!");
	}



	public SFB() {
		configure(Spec.SFB);
		spriteClass = pd.sprites.SFBSprite.class;
	}
}
