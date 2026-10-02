/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class SadSaltan extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SadSaltan.class)
			.t("name", "SadSaltan")
			.t("desc", "月光地牢的绘图者。标识有“月光”的名牌挂在他的胸前。")
			.t("yell1", "哦，难得一见的新的探险者。人们来这里就是为了寻找财宝的，没有富人会无聊到来这个地方。")
			.t("yell2", "我在其他地方开设着一个酒吧，我是过来宣传它的。想找到那里十分容易，只要对卫兵说“月光”他就会为你带路。")
			.t("yell3", "收下我的名片，以后你到那里的时候没准我可以请你喝一杯。");
	}

	public SadSaltan() {
		configure(Spec.SAD_SALTAN);
		spriteClass = pd.sprites.SadSaltanSprite.class;
	}
}
