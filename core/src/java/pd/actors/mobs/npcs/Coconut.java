/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Coconut extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Coconut.class)
			.t("name", "椰子")
			.t("desc", "一只背着单肩包头戴护目镜的橘色猫，看起来他携带有很多炸弹。")
			.t("yell1", "欢迎来到我的炸弹商店，我这儿卖各种爆炸物。")
			.t("yell2", "看起来炸弹翻译器的运行效果不错啊，小心它还可能爆炸哦。")
			.t("yell3", "你找坚果，那个hmdzl001?他在训练各种boss。短时间内你是找不到他了。");
	}

	public Coconut() {
		configure(Spec.COCONUT);
		spriteClass = pd.sprites.CoconutSprite.class;
	}
}
