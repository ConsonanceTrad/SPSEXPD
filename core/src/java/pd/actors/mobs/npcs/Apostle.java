/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Apostle extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Apostle.class)
			.t("name", "apostle")
			.t("desc", "一个闪着光芒的立方体，发声机械而又有磁性，可能是其他世界的测试者。")
			.t("yell1", "只有，魔法，才能，战胜，魔法。")
			.t("yell2", "魔法，战胜，才能，魔法，只有。")
			.t("yell3", "看来你通过了这轮测试。和其他测试者聊聊吧，没准能得到什么。");
	}

	public Apostle() {
		configure(Spec.APOSTLE);
		spriteClass = pd.sprites.ApostleSprite.class;
	}
}
