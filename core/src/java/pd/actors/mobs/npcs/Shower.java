/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Shower extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Shower.class)
			.t("name", "shower")
			.t("desc", "指虎教派的成员之一，来这里测试新的指虎武器。")
			.t("yell1", "初次见面，我是shower。你也可以叫我沐沐。虽然两个叫法都行，但是叫我沐沐我会更开心的。")
			.t("yell2", "看到东边的池塘了吗?我想你可以在那洗澡...前提在那养鱼之前。")
			.t("yell3", "这是个很棒的小镇，有很多很好的人，我想我会在这玩上一段时间。没准会碰上熟人呢。");
	}

	public Shower() {
		configure(Spec.SHOWER);
		spriteClass = pd.sprites.ShowerSprite.class;
	}
}
