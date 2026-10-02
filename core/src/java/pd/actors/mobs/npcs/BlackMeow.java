/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class BlackMeow extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BlackMeow.class)
			.t("name", "黑喵")
			.t("yell1", "哦！你好冒险者，你看上去身手不凡，不知道你有没有兴趣到我的领土上去做做客？")
			.t("yell2", "嗯…我的意思是说我的领土上也有一个地牢听说里面也有一个护符，不过里面十分危险你可能会受伤。你可以帮我带上来吗，我可以给你很多财富！")
			.t("desc", "黑喵地牢的领袖");
	}



	public BlackMeow() {
		configure(Spec.BLACK_MEOW);
		spriteClass = pd.sprites.BlackMeowSprite.class;
	}
}
