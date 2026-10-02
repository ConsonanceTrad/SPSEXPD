/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class RENnpc extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(RENnpc.class)
			.t("desc", "REN是最早使用了穿梭法术而闻名的“玩家”，据说他拿到过超过140枚Yendor护符并且分别来自不同的世界中。\n\n同时，REN也是多个地牢的创造者，虽然他现在似乎正在醉心于制作有很多美少女的地牢的样子……最好还是不要问他以前有关于“鸟船”的问题。")
			.t("name", "穿越者REN")
			.t("yell1", "有关“鸟船”，我不会再透露更多！")
			.t("yell2", "我有枚Yendor找不到了！它应该就在附近。")
			.t("yell3", "你好，我是REN，留下消息你应当看到了。我的“日志”可以复原并模拟这座地牢里传送的魔力，填满它，我会给你一件来自其他地牢的宝物。")
			.t("yell4", "我曾打造过一座充满了威士忌的地牢……听起来很浪漫不是吗？")
			.t("yell5", "感谢你完成了我留下的“日志”，虽然很不想清空老物件，但还是把这个给你吧。")
			.t("yell6", "你不会想要带着这玩意的。");
	}



	public RENnpc() {
		configure(Spec.RENNPC);
		spriteClass = pd.sprites.RENSprite.class;
	}
}
