/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class XixiZero extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(XixiZero.class)
			.t("name", "西西0.12")
			.t("desc", "黑暗地牢的制作者。")
			.t("yell1", "你好啊，我是黑暗地牢的制作者。你也可以叫我Egoal。");
	}

	public XixiZero() {
		configure(Spec.XIXI_ZERO);
		spriteClass = pd.sprites.XixiZeroSprite.class;
	}
}
