/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Dachhack extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Dachhack.class)
			.t("name", "Dachhack")
			.t("desc", "发芽地牢的作者。上面那个光环是代表自然之神地牢。")
			.t("yell1", "对发芽地牢的崩溃我表示抱歉，目前我在尝试修复他们。")
			.t("yell2", "我不明白为什么hmdzl001这个家伙会把我设计成这样...下面那是什么玩意...")
			.t("yell3", "最新的发芽你可以在我的谷歌硬盘上找到，虽然还是预览版，但是基本框架我是已经弄好了的。");
	}



	public Dachhack() {
		configure(Spec.DACHHACK);
		spriteClass = pd.sprites.DachhackSprite.class;
	}
}
