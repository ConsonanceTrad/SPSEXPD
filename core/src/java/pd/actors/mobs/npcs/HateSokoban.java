/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class HateSokoban extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HateSokoban.class)
			.t("name", "讨厌的羊关")
			.t("desc", "这是一个对发芽改有贡献的玩家，同时也是小马的爱好者。")
			.t("yell1", "你明白这是个游戏对吧，对吧。我需要完整的中文版发芽改，而不是这个半成品。")
			.t("yell2", "关于招羊法杖，虽然绝大部分时候没什么用，但是它能很好解决推箱关的难题。");
	}

	public HateSokoban() {
		configure(Spec.HATE_SOKOBAN);
		spriteClass = pd.sprites.HateSokobanSprite.class;
	}
}
