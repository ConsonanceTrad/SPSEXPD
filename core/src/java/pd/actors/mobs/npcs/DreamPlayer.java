/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class DreamPlayer extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DreamPlayer.class)
			.t("name", "小萌新大梦想")
			.t("desc", "一团七彩元素，是一个测试产物。")
			.t("yell1", "要比比看，谁更滑稽吗?")
			.t("yell2", "圆润地滑稽走。");
	}



	public DreamPlayer() {
		configure(Spec.DREAM_PLAYER);
		spriteClass = pd.sprites.DreamPlayerSprite.class;
	}
}
