/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Coconut2 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Coconut2.class)
			.t("name", "椰子")
			.t("desc", "由hmdzl所创造的一只生物...大概。")
			.t("yell1", "我急了我急了，这之后该怎么办啊。")
			.t("yell2", "讲道理啊，人员引进和安排都是由我处理的，也不给我放个假什么的。")
			.t("yell3", "之后的计划???谁知道之后会发生什么呢。");
	}

	public Coconut2() {
		configure(Spec.COCONUT2);
		spriteClass = pd.sprites.CoconutSprite.class;
	}
}
