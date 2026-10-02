/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class ARealMan extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ARealMan.class)
			.t("name", "炼金矮人")
			.t("desc", "工会中的一个奇特的存在，擅长炼金术和收藏独立游戏。");
	}



	public ARealMan() {
		configure(Spec.A_REAL_MAN);
		spriteClass = pd.sprites.ARealManSprite.class;
	}
}
