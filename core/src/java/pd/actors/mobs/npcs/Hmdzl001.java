/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Hmdzl001 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Hmdzl001.class)
			.t("name", "hmdzl001")
			.t("desc", "这个游戏的缔造者，同时也是一位玩家。")
			.t("yell1", "我该说欢迎吗，这是为2019春节专门准备的地图，但是就算是2022年，世界依然没有好转。")
			.t("yell2", "椰子...它会处理之后的一切。作为一台机器，它很可爱，但它并不能缓解我的忙碌。")
			.t("yell3", "2022春节...操蛋极了，我一天都没好好休息。现在就算不是春节这里也会持续开放。");
	}

	public Hmdzl001() {
		configure(Spec.HMDZL001);
		spriteClass = pd.sprites.Hmdzl001Sprite.class;
	}
}
