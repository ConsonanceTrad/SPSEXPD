/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class FlyLing extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FlyLing.class)
			.t("name", "澪")
			.t("desc", "谁也不知道她从何处来，她偶尔会去雪景旅馆休息。\n\n如果你遇到她了，不妨试试和她搭讪看看吧。")
			.t("yell1", "嗯？你好啊。欢迎你来到这里，我是澪。")
			.t("yell2", "你问我是谁？谁知道呢，但我偶尔会来这里休息，并在这里祝福每一个冒险者。")
			.t("yell3", "你会获得救赎吗？愿世界祝福你……");
	}



	public FlyLing() {
		configure(Spec.FLY_LING);
		spriteClass = pd.sprites.WhiteLingSprite.class;
	}
}
