/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Lynn extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Lynn.class)
			.t("desc", "十分出色的符文研究者，对各种派系的魔法都有研究。其中最为出色的研究就是多利亚出产的魔法石。")
			.t("name", "符文学者-莲恩")
			.t("yell1", "我已经修好了这个合成台，现在你应该可以使用魔法石合成武器了。")
			.t("yell2", "我的下一个研究目标应该是什么呢?")
			.t("yell3", "这不是你该有的东西~~~");
	}

	public Lynn() {
		configure(Spec.LYNN);
		spriteClass = pd.sprites.LynnSprite.class;
	}
}
