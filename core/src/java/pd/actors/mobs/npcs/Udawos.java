/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Udawos extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Udawos.class)
			.t("name", "Udawos")
			.t("desc", "先锋的制作者。先锋是以像素地牢的源代码制作的。")
			.t("yell1", "我的新游戏叫做chernog：FOMTMA。你可以在网上下载这个游戏。")
			.t("yell2", "先锋不同于传统地牢游戏，它是一个rpg游戏，和塞尔达传说1类似。");
	}

	public Udawos() {
		configure(Spec.UDAWOS);
		spriteClass = pd.sprites.UdawosSprite.class;
	}
}
